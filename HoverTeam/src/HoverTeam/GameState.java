package HoverTeam;
/**
 * HoverTeam
 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
//package HoverTeam;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;


public class GameState implements Serializable {
	private static final long serialVersionUID = -7380811253868690573L;
	private double x, y, theta;	/*Shared resource*/
	private double x_dot, y_dot, theta_dot;	/*Shared resource*/
	private int n_players;
	private final double thrusterWidth = Physics.length*Display.sF/10;
	/**
	 * The time since the game start [seconds].
	 */
	private double t;

	/* near obstacles list */
	private int[] near_obst_heights;
	private int near_obst_start_i;

	//defaults to true and if we 
	private boolean gameOutcome;

	//constants
	private final int num_heights_in_near_list=4;
	private final int obstacle_width=1;
	private final int obstacle_spacing=5;
	public final double MIN_LINEAR_VEL = -10;
	public final double MAX_LINEAR_VEL = 10;
	public final double MIN_ANGULAR_VEL = -Math.PI/4;
	public final double MAX_ANGULAR_VEL = Math.PI/4;

	public GameState(double[] position, double[] velocity, double time, int nPlayers, int[] nearObstHeights, int nearObstIndex){
		if (position.length != 3)
			throw new IllegalArgumentException("Positon array must be of length 3"); 
		if (velocity.length != 3)
			throw new IllegalArgumentException("Velocity array must be of length 3"); 
		setPosition(position);

		setVelocity(velocity);
		t = time;
		n_players = nPlayers;
		near_obst_heights = nearObstHeights;
		near_obst_start_i = nearObstIndex;
		gameOutcome = true;	
	}
	synchronized public void setTime(double new_time) {
		if(new_time>0) {
			t = new_time;
		}
	}
	synchronized public double getTime() {
		return t;
	}
	synchronized public void setPosition(double[] newPosition) {
		if (newPosition.length != 3)
			throw new IllegalArgumentException("new position array must be of length 3");      

		x = newPosition[0];
		y = newPosition[1];
		theta = newPosition[2];

		clampPosition();
	}
	private void clampPosition() {
		x = Math.min(Math.max(x,0),1e6);	//calling 1e6= MAX_DOUBLE from SRS
		y = Math.min(Math.max(y,0),11);
		theta = Math.min(Math.max(theta, -Math.PI), Math.PI);
		if (theta - Math.PI == 0 || Math.abs(theta - Math.PI) < 1e-6)
			theta = -Math.PI;
		//might need better bounding of theta
	}
	synchronized public double[] getPosition(){
		double[] position = new double[3];
		position[0]=x;
		position[1]=y;
		position[2]=theta;
		return position;
	}  
	synchronized public void setVelocity(double[] newVelocity) {
		if (newVelocity.length != 3)
			throw new IllegalArgumentException("new Velocity array must be of length 3");      

		x_dot= newVelocity[0];
		y_dot = newVelocity[1];
		theta_dot = newVelocity[2];		

		clampVelocity();
	}
	private void clampVelocity() {
		x_dot = Math.min(Math.max(x_dot,MIN_LINEAR_VEL),MAX_LINEAR_VEL);	
		y_dot = Math.min(Math.max(y_dot,MIN_LINEAR_VEL),MAX_LINEAR_VEL);
		theta_dot = Math.min(Math.max(theta_dot, MIN_ANGULAR_VEL), MAX_ANGULAR_VEL);
	}
	synchronized public double[] getVelocity(){
		double[] velocity = new double[3];
		velocity[0]=x_dot;
		velocity[1]=y_dot;
		velocity[2]=theta_dot;
		return velocity;
	}  
	synchronized int[] getNearObstList(){
		return near_obst_heights;
	}
	public void setNumPlayers(int new_num_players){
		n_players= new_num_players;
	}
	public int getNumPlayers(){
		return n_players;
	}
	public boolean getGameOutcome() {
		return gameOutcome;
	}
	private void computeNearObstList(int[] allObstaclesList){
		int[] nearList = new int[num_heights_in_near_list];
		for (int i=near_obst_start_i;i<(num_heights_in_near_list+near_obst_start_i);i++){
			nearList[i-near_obst_start_i]=allObstaclesList[i];
		}
	}
	/**
	 * Checks if the vehicle is in collision with an obstacle.
	 * If the vehicle is in collision, gameOutcome is set to false.
	 */
	public void checkCollisions(){
		// Get the vehicle perimeter shape
		Path2D currentVehic = getVehicleShapePath(x,y,1);

		// Check collisions between the vehicle shape path and the obstacles 
		for (int i=0; i<near_obst_heights.length;i++){
			Rectangle2D.Double rectangle = new Rectangle2D.Double(
					(near_obst_start_i+i)*obstacle_spacing, //x bottom left corner
					0, //y bottom left corner
					obstacle_width,  //width
					near_obst_heights[i] //height
					);
			if (testIntersection(currentVehic, rectangle)) {
				gameOutcome=false;	//you lose.
			}
		}

		// Check collisions between the vehicle and the ceiling
		Rectangle2D.Double ceiling = new Rectangle2D.Double(
				(near_obst_start_i-2)*obstacle_spacing, //x bottom left corner
				10, //y bottom left corner
				obstacle_spacing*(near_obst_heights.length+4),  //width
				1 //height
				);
		if (testIntersection(currentVehic, ceiling)) {
			gameOutcome=false;	//you lose.
		}
		// Check collisions between the vehicle and the floor
		Rectangle2D.Double floor = new Rectangle2D.Double(
				(near_obst_start_i-2)*obstacle_spacing, //x bottom left corner
				-1, //y bottom left corner
				obstacle_spacing*(near_obst_heights.length+4),  //width
				1 //height
				);
		if (testIntersection(currentVehic, floor)) {
			gameOutcome=false;	//you lose.
		}
	}

	/**
	 * Gets a Path2D which represents the perimeter of the vehicle in
	 * the world reference frame.
	 * @return
	 */
	public Path2D.Double getVehicleShapePath(double x, double y, int sF) {
		double vehicWidth=Physics.length*sF;
		double vehicHeight= Physics.height*sF;
		/*
		 * vehicle center is x,y
		 * setting 
		 */
		double[] bottomLeft = new double[2];
		bottomLeft[0]=x-vehicWidth/2;
		bottomLeft[1]=y-vehicWidth/2;
		
		Rectangle2D.Double currentVehic = new Rectangle2D.Double(bottomLeft[0], bottomLeft[1],vehicWidth, vehicHeight);
		// Rotate the vehicle perimeter about its center
		AffineTransform rotate = new AffineTransform();
		//Rectangle2D rotatedVehic = AffineTransform.getRotateInstance(theta,x,y);
		rotate.setToRotation(theta, x, y);
		//currentVehic.setTransform(rotate);
		Path2D.Double rotatedVehic = new Path2D.Double(currentVehic, rotate);
		return rotatedVehic;
	}
	public Path2D.Double getVehicleBottomLine(double x, double y, int sF){
		double vehicWidth=Physics.length*sF;
		double vehicHeight= Physics.height*sF;
		double[] bottomLeft = new double[2];
		double[] bottomRight = new double[2];
		bottomLeft[0]=x-vehicWidth/2;
		bottomLeft[1]=y-vehicWidth/2;

		bottomRight[0]=x+vehicWidth/2;
		bottomRight[1]= y-vehicHeight/2;
		Rectangle2D.Double bottomLine = new Rectangle2D.Double(bottomLeft[0], bottomLeft[1], vehicWidth, vehicHeight/5);
		AffineTransform rotate = new AffineTransform();
		//Rectangle2D rotatedVehic = AffineTransform.getRotateInstance(theta,x,y);
		rotate.setToRotation(theta, x, y);
		//currentVehic.setTransform(rotate);
		Path2D.Double rotatedLine = new Path2D.Double(bottomLine, rotate);
		return rotatedLine;
	
	}
	public Path2D.Double[] getThrusterLocations(double x, double y, int sF){
		Path2D.Double[] thrusters = new Path2D.Double[n_players];
		double vehicWidth=Physics.length*sF;
		double vehicHeight= Physics.height*sF;
		double[] bottomLeft = new double[2];
		double[] bottomRight = new double[2];
		bottomLeft[0]=x-vehicWidth/2;
		bottomLeft[1]=y-vehicWidth/2;

		bottomRight[0]=x+vehicWidth/2;
		bottomRight[1]= y-vehicHeight/2;
		if (n_players<2){
			Rectangle2D.Double thruster = new Rectangle2D.Double(bottomLeft[0]+vehicWidth/2, bottomLeft[1]-vehicHeight/5, thrusterWidth, vehicHeight/5);
			AffineTransform rotate = new AffineTransform();
			rotate.setToRotation(theta, x, y);
			Path2D.Double rotatedThruster = new Path2D.Double(thruster, rotate);
			thrusters[0] = rotatedThruster;
			
		}
		else{
			for (int i = 1;i<=thrusters.length;i++){
				Rectangle2D.Double thruster = new Rectangle2D.Double(bottomLeft[0]+(vehicWidth-thrusterWidth)*(i-1)/(n_players-1), bottomLeft[1]-vehicHeight/5, thrusterWidth, vehicHeight/5);
				AffineTransform rotate = new AffineTransform();
				//Rectangle2D rotatedVehic = AffineTransform.getRotateInstance(theta,x,y);
				rotate.setToRotation(theta, x, y);
				//currentVehic.setTransform(rotate);
				Path2D.Double rotatedThruster = new Path2D.Double(thruster, rotate);
				thrusters[i-1] = rotatedThruster;
			}
		}
		
		return thrusters;
		
	}

	/**
	 * Returns true if the two shapes intersect.
	 * @param shape1
	 * @param shape2
	 * @return
	 */
	public static boolean testIntersection(Shape shape1, Shape shape2){
		Area a1 = new Area(shape1);
		a1.intersect(new Area(shape2));
		return !a1.isEmpty();
	}

}