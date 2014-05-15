/**
 * HoverTeam
 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
package HoverTeam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GameClient implements Runnable {
	/**
	 * The latest GameState (updated by GameClientReceiver).
	 */
	private GameState state;
	/**
	 * The socket for sending controls messages.
	 */
	private DatagramSocket controls_socket;
	/**
	 * The IP address of the GameServer machine.
	 */
	private InetAddress server_ip_addr;

	public GameClient(String host) {
		try {
			server_ip_addr = InetAddress.getByName(host);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		int port = GameServerReceiver.controls_port_send;
		while( controls_socket == null ) {
			try {
				controls_socket = new DatagramSocket(port);

			} catch (SocketException e) {
				e.printStackTrace();
			}
			port++;
		}
	}
	/**
	 * Convert a serialized data array into an Object.
	 * The GameServer broadcasts the game state data to the GameClient over UDP.
	 * This method is used to decode that data into a GameState Object.
	 * @param data
	 * @return The data converted into a java Object.
	 */
	public static Object deserialize(byte[] data){
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is;
		try {
			is = new ObjectInputStream(in);
			return is.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void setState(GameState state) {
		if(state != null) {
			this.state = state;
		}
	}

	public synchronized GameState getState() {
		return this.state;

	}

	/**
	 * This method is to be called by Display when the user presses a key to
	 *  turn her/his thruster on.
	 */
	public void thrusterOn() {
		System.out.println("Thruster ON");
		// Make a controls packet.
		byte[] data = {1};
		DatagramPacket packet = new DatagramPacket(
				data, data.length, server_ip_addr, GameServerReceiver.controls_port_rcv);
		// Send the controls packet to the server.
		try {
			controls_socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is to be called by Display when the user presses a key to 
	 * turn her/his thruster off.
	 */
	public void thrusterOff() {
		System.out.println("Thruster OFF");
		// Make a controls packet.
		byte[] data = {0};
		DatagramPacket packet = new DatagramPacket(
				data, data.length, server_ip_addr, GameServerReceiver.controls_port_rcv);
		// Send the controls packet to the server.
		try {
			controls_socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] argv) throws IOException{
		if (argv.length == 0) {
			System.err.println("Usage: GameClient <hostname>\n"+
					"where <hostname> is where GameServer is running");
			System.exit(-1);
		}
		String host=argv[0];
		// Make the GameClient
		GameClient gc = new GameClient(host);
		// Make the GameClientReceiver to get the GameState broadcasts.
		GameClientReceiver gcr = new GameClientReceiver(gc);
		// make the display
		Display disp = new Display();
		disp.setGameClient(gc);
		disp.setup();
		// Start the Client-side threads
		Thread gc_thread = new Thread(gc);
		Thread gcr_thread = new Thread(gcr);
		Thread disp_thread = new Thread(disp);
		gc_thread.start();
		gcr_thread.start();
		disp_thread.start();
		// Wait for the GameClient thread to finish
		try {
			gc_thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Kill the GameClientReceiver
		System.out.println("Client shutting down...");
		gcr.on = false;
		try {
			gcr_thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true) {
			GameState state = getState();
			if(state != null) {
				if(!state.getGameOutcome()) {break;}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

