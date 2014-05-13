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
		byteStream = new ByteArrayOutputStream();
		try {
			objectStream = new ObjectOutputStream(byteStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			socket = new DatagramSocket(4445);
		} catch (SocketException e) {
			e.printStackTrace();
		}		
	}
	
	public byte[] serialize(Object o) {
		try {
			objectStream.writeObject(o);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return byteStream.toByteArray();
	}

	@Override
	public void run() {
		System.out.println("Server up");
		for(int i=0; i < 10; i++){
			byte[] buf = new byte[256];
			String dString = String.format("Message %d", i);
			buf = dString.getBytes();
			InetAddress group;
			try {
				group = InetAddress.getByName("224.0.0.1");
				DatagramPacket packet;
				packet = new DatagramPacket(buf, buf.length, group, 4446);
				try {
					socket.send(packet);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
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
