package HoverTeam;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * This helper class handles the receipt of GameState messages from the GameServer.
 * @see Req. 3.2.3.8
 */
public class GameClientReceiver implements Runnable {
	public boolean on = true;
	private GameClient gc;

	public GameClientReceiver(GameClient gc) {
		this.gc = gc;
	}
	
	public void run() {
		try{
			MulticastSocket socket = new MulticastSocket(GameServer.multicast_port_rcv);
			InetAddress group = InetAddress.getByName(GameServer.multicast_ip_addr);
			socket.joinGroup(group);
			System.out.println("Client up");
			DatagramPacket packet;
			while(on) {
				byte[] buf = new byte[1024];
				// Receive a packet
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				// Get the data out of the packet
				byte[] data = packet.getData();
				// Reconstruct the data into a GameState
				GameState state = (GameState) GameClient.deserialize(data);
				// Set the GameClient's state
				this.gc.setState(state);
				//System.out.println(state.getTime());
			}

			socket.leaveGroup(group);
			socket.close();
			System.out.println("Client socket closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
