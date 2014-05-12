/**
 * HoverTeam
 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
package HoverTeam;

public class GameServer implements Runnable{
	/** 
	 * The state of the game.
	 */
	protected GameState state;
	/**
	 * The latest control inputs from each player
	 */
	private boolean[] controls;
	
	public GameServer() {
		
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
		// TODO Auto-generated method stub
		
	}
}
