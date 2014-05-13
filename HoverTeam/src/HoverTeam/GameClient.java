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
import java.net.InetAddress;
import java.net.MulticastSocket;

public class GameClient implements Runnable {
	private GameState state;

	public GameClient() {

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

	public static void main(String[] argv) {
		GameClient gc = new GameClient();
		GameClientReceiver gcr = new GameClientReceiver(gc);
		// Start the Client-side threads
		Thread gc_thread = new Thread(gc);
		Thread gcr_thread = new Thread(gcr);
		gc_thread.start();
		gcr_thread.start();
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
				System.out.println(state.getTime());
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

