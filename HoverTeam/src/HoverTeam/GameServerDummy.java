package HoverTeam;

public class GameServerDummy extends GameServer {

	public GameServerDummy() {
		boolean[] controls = {false};
		this.setControls(controls);
		int[] nearObstHeights = {1};
		int nearObstIndex = 0;
		double[] pos = {5, 5, 0};
		double[] vel = {0, 0, 0};
		this.setState(new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex));
		
	}
	
	@Override
	public void run() {
		boolean[] controls = {true, true};
		this.setControls(controls);
		while(this.getState().getTime() < 10 
				&& this.getState().getGameOutcome()) {
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
	}
	
	public static void main(String[] argv) {
		GameServer server = new GameServerDummy();
		Physics phys = new Physics(server);
		System.out.println("running Physics");
		Thread phys_thread = new Thread(phys);
		phys_thread.start();
		System.out.println("running Server");
		Thread server_thread = new Thread(server);
		server_thread.start();
		server.run();		
	}
	

}
