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
		boolean[] controls = {true, true};
		this.setControls(controls);
		while(this.getState().getTime() < 10 
				&& this.getState().getGameOutcome()) {
			broadcastState();
		System.out.println(String.format(
				"t=%3fs x=%.3fm, y=%.3fm",
				this.getState().getTime(),
				this.getState().getPosition()[0],
				this.getState().getPosition()[1]));
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		multicast_socket.close();
	}
}
