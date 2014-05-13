package HoverTeam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MulticastServer implements Runnable{
	DatagramSocket socket;
	ByteArrayOutputStream byteStream;

	ObjectOutputStream objectStream;

	public MulticastServer() {
		
		try {
			socket = new DatagramSocket(4445);
		} catch (SocketException e) {
			e.printStackTrace();
		}		
	}

	public byte[] serialize(Object o) {
		byteStream = new ByteArrayOutputStream();
		try {
			objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(o);
			objectStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteStream.toByteArray();
	}

	@Override
	public void run() {
		// Set up the group to multicast to
		InetAddress group = null;
		try {
			group = InetAddress.getByName("224.0.0.1");
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
		System.out.println("Server up");
		// Make a new GameState to send
		int[] nearObstHeights = {1};
		int nearObstIndex = 0;
		double[] pos = {5, 5, 0};
		double[] vel = {0, 0, 0};
		GameState state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);

		for(int i=0; i < 10; i++){
			// Log the cycle count in the GameState time
			double time = 1.0*i;
			System.out.println(time);
			state.setTime(time);
			// Convert the GameState into a byte array
			byte[] data = serialize(state);
			// Pack the byte array into a packet
			DatagramPacket packet;
			packet = new DatagramPacket(data, data.length, group, 4446);
			// Send the packet
			try {
				socket.send(packet);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} 
			catch (InterruptedException e) { }
		}
		socket.close();
	}

	public static void main(String args[]) {
		MulticastServer server = new MulticastServer();
		Thread server_thread = new Thread(server);
		server_thread.start();
		try {
			server_thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
