package HoverTeam;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastClient {
	public static void main(String args[]) {
		try{
			MulticastSocket socket = new MulticastSocket(4446);
			InetAddress group = InetAddress.getByName("224.0.0.1");
			socket.joinGroup(group);
			System.out.println("Client up");
			DatagramPacket packet;
			for (int i = 0; i < 10; i++) {
				byte[] buf = new byte[256];
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String received = new String(packet.getData());
				System.out.println(received);
			}

			socket.leaveGroup(group);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
