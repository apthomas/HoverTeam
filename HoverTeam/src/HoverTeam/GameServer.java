/**
 * HoverTeam
 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
package HoverTeam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class GameServer implements Runnable{
	/** 
	 * The state of the game.
	 */
	protected GameState state;
	/**
	 * The latest control inputs from each player
	 */
	private boolean[] controls;
	/**
	 * Multicast IP address for broadcasting the GameState.
	 */
	public static final String multicast_ip_addr = "224.0.0.1";
	/**
	 * Multicast UDP port for broadcasting the GameState.
	 * This is the receiving port.
	 */
	public static final int multicast_port_rcv = 4445;
	/**
	 * Multicast UDP port for broadcasting the GameState.
	 * This is the sending port.
	 */
	public static final int multicast_port_send = 4446;
	/**
	 * Multicast UDP Socket for broadcasting the GameState.
	 */
	DatagramSocket multicast_socket;
	/**
	 * Multicast IP address group for broadcasting the GameState.
	 */
	InetAddress multicast_group = null;
	/**
	 * The list of all obstacles. Each obstacle is represented by an
	 * integer defining its height.
	 */
	private ArrayList<Integer> all_obst;

	public GameServer() {
		// Set up the socket for broadcasting the GameState.
		try {
			multicast_socket = new DatagramSocket(multicast_port_send);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		// Set up the multicast IP address group for broadcasting the GameState.
		try {
			multicast_group = InetAddress.getByName(multicast_ip_addr);
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
		// Initialize the obstacles.
		all_obst = new ArrayList<Integer>();
		// Initialize the controls inputs.
		boolean[] controls = {false};
		this.setControls(controls);
		// Initialize the GameState.
		int[] nearObstHeights = {1};
		int nearObstIndex = 0;
		double[] pos = {-2, 8.5, 0};
		double[] vel = {0.1, 0, 0};
		this.setState(new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex));
		// Initialize the obstacles.
		all_obst = new ArrayList<Integer>();
	}

	public static byte[] serialize(Object o) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(o);
			objectStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteStream.toByteArray();
	}

	/**
	 * Generate the height for a new Obstacle
	 * @return the height
	 * @see Req. 3.2.2.3
	 */
	public int makeObstacle() {
		Random randomGenerator = new Random();
		return randomGenerator.nextInt(8);
	}
	
	/**
	 * 
	 * @param all_obst The list of all obstacles. It is modified by this method.
	 * @param currentX
	 * @return An array of the heights of nearby obstacles
	 * @see Req. 3.2.2.4
	 */
	public int[] generateNearList(ArrayList<Integer> all_obst, double currentX) {
		// How far in the x direction the all_obst list reaches.
		double all_extent_x = (all_obst.size()-2)*GameState.obstacle_spacing;
		double near_extent_x = GameState.num_heights_in_near_list * GameState.obstacle_spacing;
		while ( currentX - all_extent_x > -near_extent_x/2) {
			// We need to make more obstacles
			all_obst.add(makeObstacle());
			all_extent_x = (all_obst.size()-2)*GameState.obstacle_spacing;
			System.out.println(String.format(
					"all_extent_x = %.2fm",
					all_extent_x));
		}
		int near_obst_start_i = 
				(int) (Math.floor(currentX/GameState.obstacle_spacing)
				- GameState.num_heights_in_near_list/2) + 1;
		if (near_obst_start_i < 0) { near_obst_start_i = 0; }
		int[] near_list = new int[GameState.num_heights_in_near_list];
		for (int i=near_obst_start_i;
				i<(GameState.num_heights_in_near_list+near_obst_start_i); i++){
			near_list[i-near_obst_start_i] = all_obst.get(i);
		}
		
		// push the new nearlist on the state
		synchronized(this) {
		GameState state = getState();
		if(state != null) {
			state.setNearObst(near_list);
			state.setNearObstStartI(near_obst_start_i);
			setState(state);
		}
		}
		
		return near_list;
	}
	/**
	 * Broadcast the GameState to the Clients using UDP Multicast.
	 */
	public void broadcastState() {
		// Convert the GameState into a byte array
		byte[] data = serialize(this.getState());
		// Pack the byte array into a packet
		DatagramPacket packet = 
				new DatagramPacket(data, data.length, multicast_group, multicast_port_rcv);
		// Send the packet
		try {
			multicast_socket.send(packet);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		//System.out.println("sent state");
	}

	public synchronized void setState(GameState state) {
		if(state != null) {
			this.state = state;
		}
	}

	public synchronized GameState getState() {
		return this.state;

	}

	public synchronized boolean[] getControls() {
		return this.controls;
	}

	public synchronized void setControls(boolean[] controls) {
		if(controls != null) {
			this.controls  = controls;
		}
	}

	public void run() {
		while(this.getState().getGameOutcome()) {
			generateNearList(all_obst, this.getState().getPosition()[0]);
			broadcastState();
			/*
			System.out.println(String.format(
				"t=%3fs x=%.3fm, y=%.3fm",
				this.getState().getTime(),
				this.getState().getPosition()[0],
				this.getState().getPosition()[1]));
			*/
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		multicast_socket.close();
	}
	public static void main(String[] argv) {
		GameServer server = new GameServer();
		GameServerReceiver gsr = new GameServerReceiver(server);
		Physics phys = new Physics(server);
		// Make and start the server-side threads.
		System.out.println("running Server...");
		Thread server_thread = new Thread(server);
		server_thread.start();
		System.out.println("running ServerReceiver...");
		Thread gsr_thread = new Thread(gsr);
		gsr_thread.start();
		System.out.println("running Physics...");
		Thread phys_thread = new Thread(phys);
		phys_thread.start();			
	}
}
