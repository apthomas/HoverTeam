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
public class Physics {
	/**
	 * The latest GameState.
	 * @see Req 3.2.1.1.1
	 */
	//private GameState gs;
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
	 * Constructor.
	 * @see Req 3.2.1.2
	 */
	public Physics() {
	}
	
	/**
	 * Updates the Game State. Control inputs are used to generate a system of
	 * forces and torques on the vehicle, and these are used to update the physical 
	 * state of the vehicle though a vehicle dynamics model.
	 * @see Req 3.2.1.3
	 * @param time
	 * @param controls
	 */
	public void UpdateState(double time, boolean controls[]){
		/* Generate a system of forces and torques on the vehicle. 
		 * Req 3.2.1.3.1.
		 */
		double forces[] = getForces(controls);
		double torque = getTorque(controls);
		/* Update the state of the vehicle, 
		 * using a model of the vehicle dynamics and the applied forces/torques
		 * Req 3.2.1.3.1
		 */
		
		/* Check if the current position of the vehicle is in
		 * collision with an obstacle.
		 * Req 3.2.1.3.4
		 */
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

}
