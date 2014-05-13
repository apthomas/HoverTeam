package HoverTeam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastClient {
	
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
	public static void main(String args[]) {
		try{
			MulticastSocket socket = new MulticastSocket(GameServer.multicast_port_rcv);
			InetAddress group = InetAddress.getByName(GameServer.multicast_ip_addr);
			socket.joinGroup(group);
			System.out.println("Client up");
			DatagramPacket packet;
			for (int i = 0; i < 10; i++) {
				byte[] buf = new byte[1024];
				// Receive a packet
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				// Get the data out of the packet
				byte[] data = packet.getData();
				// Reconstruct the data into a GameState
				GameState state = (GameState) deserialize(data);
				System.out.println(state.getTime());
			}

			socket.leaveGroup(group);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
