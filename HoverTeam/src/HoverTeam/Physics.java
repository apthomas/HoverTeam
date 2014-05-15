/**
 * HoverTeam
 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
package HoverTeam;

/**
 * The physics engine for the game.
 * @see Req 3.2.1
 */
public class Physics implements Runnable{
	/**
	 * Reference to the GameServer
	 */
	GameServer server;
	/**
	 * The last time the state was updated.
	 * Measured in seconds from the game start.
	 */
	private double t_last = 0;
	/**
	 * The thrust of each thruster [Newtons].
	 */
	public final double F_thruster = 1;
	/**
	 * The vehicle mass [kilograms].
	 */
	public final double m = 1;
	
	/**
	 * The acceleration due to gravity [meters/second^2].
	 */
	public final double g = 1;
	/**
	 * The vehicle length [meters].
	 */
	public static final double length = 1;
	/**
	 * The Vehicle height [meters].
	 */
	public static final double height = 1;	
	/**
	 * The vehicle moment of inertia [kg m^2]
	 * Assume the vehicle mass is uniformly distributed along its length.
	 */
	public final double I = m*length*length/12;
	/**
	 * The timestep between physics updates [seconds]
	 */
	public final double timestep = 1e-2;
	
	/**
	 * Constructor.
	 * @see Req 3.2.1.2
	 */
	public Physics( GameServer server ) {
		this.server = server;
	}
	
	/**
	 * Updates the Game State. Control inputs are used to generate a system of
	 * forces and torques on the vehicle, and these are used to update the physical 
	 * state of the vehicle though a vehicle dynamics model.
	 * @see Req 3.2.1.3
	 * @param time
	 * @param controls
	 */
	public GameState updateState(GameState state, boolean controls[]){
		/* Generate a system of forces and torques on the vehicle. 
		 * Req 3.2.1.3.1.
		 */
		double forces[] = getForces(controls);
		double torque = getTorque(controls);
		/* Update the state of the vehicle, 
		 * using a model of the vehicle dynamics and the applied forces/torques
		 * Req 3.2.1.3.1
		 */
		GameState new_state = dynamics(state, forces, torque);
		/* Check if the current position of the vehicle is in
		 * collision with an obstacle.
		 * Req 3.2.1.3.4
		 */
		new_state.checkCollisions();
		return state;
	}	
	
	GameState dynamics(GameState state, double[] forces, double torque) {
		double dt = state.getTime() - t_last;
		// record the time for the last state update
		t_last = state.getTime();
		//System.out.println(dt);
		// Linear 
		double[] accel = {0,0};
		double[] vel = state.getVelocity();
		double[] pos = state.getPosition();
		for(int i=0; i<2; i++){
			accel[i] = forces[i] / m;
			vel[i] += accel[i] * dt;
			pos[i] += vel[i] * dt;
		}
		// Angular
		double ang_accel = torque / I;
		vel[2] += ang_accel * dt;
		pos[2] += vel[2] * dt;
		
		state.setPosition(pos);
		state.setVelocity(vel);
		return state;
	}
	
	/**
	 * Compute the forces on the vehicle exerted on the vehicle by
	 * gravity and thrusters, given control input.
	 * @param controls Indicates which thrusters are on (true=on).
	 * @return The forces (Fx, Fy) on the vehicle in the world frame [Newtons].
	 */
	double[] getForces(boolean controls[]){
		double[] F = {0,0};
		// Thruster force
		double theta = 0; //TODO
		for(boolean on : controls){
			if(on) {
				F[0] += -Math.sin(theta) * F_thruster;
				F[1] += Math.cos(theta) * F_thruster;
			}
		}
		// Gravity force
		F[1] += -g*m;
		return F;
	}
	
	/**
	 * Compute the torque exerted on the vehicle by the thrusters,
	 *  given a control input which indicates which thrusters are on.
	 * Positive torques produce counterclockwise rotation.
	 * @param controls IIf the ith value fo this array is true, the ith thruster is on.
	 * @return The torque on the vehicle [Newton meters].
	 */
	double getTorque(boolean controls[]){
		double[][] thruster_pos = Physics.getThrusterPositions(controls.length);
		double torque = 0;
		for(int i=0; i<controls.length; i++) {
			if(controls[i]) {
				torque += F_thruster * thruster_pos[i][0];
			}
		}
		return torque;
	}
	
	/**
	 * get the positions of the thrusters on the vehicle, given how many thruster there are.
	 * @param n The number of thrusters.
	 * @return The thruster positions relative to the vehicle center 
	 * in the vehicle frame [meters].
	 */
	public static double[][] getThrusterPositions(int n){
		if(n<1){
			throw new IllegalArgumentException(
					"The number of thusters must be an integer greater than 0.");
		}
		double thruster_pos[][] = new double[n][2];
		for(int i = 0; i < n; i++) {
			thruster_pos[i][0] = (i-Math.floor(n/2))/(Math.floor(n/2)) * Physics.length/2;
			thruster_pos[i][1] = -Physics.height/2;
		}
		return thruster_pos;
	}

	@Override
	public void run() {
		// Record the start time.
		double t_start_abs = System.nanoTime()*1e-9;
		System.out.println("Physics is up");
		// Run the physics until the player(s) lose.
		GameState state = server.getState();
		while(state.getGameOutcome()) {
			// Get the latest GameState from the server
			state = server.getState();
			// Record the time at which this cycle started, relative to the start of the game.
			double t_cycle_start = System.nanoTime()*1e-9 - t_start_abs;
			//System.out.println(String.format("t_cycle_start=%.6fs", t_cycle_start));
			state.setTime(t_cycle_start);
			// Get the latest control inputs from the GameServer
			boolean[] controls = server.getControls();
			// Update the GameState
			state = updateState(state, controls);
			// push the new GameState to the server
			server.setState(state);
			
			// Sleep until the next cycle
			double t_spent = (System.nanoTime()*1e-9 - t_start_abs) - t_cycle_start;
			double t_sleep = timestep - t_spent;
			//System.out.println(String.format("t_sleep=%.3fms", t_sleep*1000));
			if(t_sleep > 0) {
				try {
					long t_sleep_millis = (long) Math.floor(t_sleep*1000.0);
					int t_sleep_nanos = (int) ((t_sleep%0.001) * 1e9);
					Thread.sleep(t_sleep_millis, t_sleep_nanos);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
