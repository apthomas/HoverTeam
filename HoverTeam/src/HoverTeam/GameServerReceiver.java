/**
 * HoverTeam
 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
package HoverTeam;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class GameServerReceiver  implements Runnable {
	/**
	 * The IP addresses of the clients connected to the game.
	 */
	private ArrayList<InetAddress> ip_addrs;
	/**
	 * the latest control for each client.
	 */
	private ArrayList<Boolean> controls;
	/**
	 * The port for receiving controls messages.
	 */
	public final static int controls_port_rcv = 4447;
	/**
	 * The port for sending controls messages.
	 */
	public final static int controls_port_send = 4448;
	/**
	 * The socket for receiving controls messages.
	 */
	private DatagramSocket socket;
	/**
	 * The GameServer.
	 */
	GameServer gs = null;

	public GameServerReceiver(GameServer gs) {
		this.gs = gs;
		ip_addrs = new ArrayList<InetAddress>();
		controls = new ArrayList<Boolean>();
		try {
			socket = new DatagramSocket(controls_port_rcv);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("ServerReceiver is up.");
		while(true) { // Infinite loop is bad, we should fix this.
			// Set up to receive a packet.
			byte[] data = new byte[1];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			// Wait for a packet to come in.
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Process the packet.
			int client_index;
			if( ip_addrs.contains(packet.getAddress()) ){
				// If the Sever already knows this client.
				client_index = ip_addrs.indexOf(packet.getAddress());
			}
			else {
				/* If the server does not know the client, add the client to the
				 * list of known clients. */
				client_index = ip_addrs.size();
				ip_addrs.add(packet.getAddress());
				controls.add(false);
				GameState a = gs.getState();
				a.setNumPlayers(ip_addrs.size()+1);
				gs.setState(a);
			}
			if (data[0] == 0) {
				System.out.println("ServerRcv: Thruster OFF");
				controls.set(client_index, false);
			}
			else if (data[0] == 1) {
				System.out.println("ServerRcv: Thruster ON");
				controls.set(client_index, true);
			}
			else {
				System.out.println("ServerRcv: Received bad control packet");
			}
			// Send the controls list to the GameServer.
			boolean[] controls_primitive = new boolean[controls.size()];
			for(int i = 0; i < controls.size(); i++){
				controls_primitive[i] = controls.get(i);
			}
			if(gs != null) {
				gs.setControls(controls_primitive);
			}
			// Print the received controls
			/*
			System.out.print("ServerRcv controls: [ ");
			for(boolean b : controls) {
				System.out.print(b + " ");
			}
			System.out.println("]");
			*/
		}
	}

	public static void main(String[] args) {
		GameServerReceiver gsr = new GameServerReceiver(null);
		Thread gsr_thread = new Thread(gsr);
		gsr_thread.start();
	}

}
