/**
 * HoverTeam


 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
//package HoverTeam;

public class GameState {
	private double x, y, theta;	/*Shared resource*/
	private double x_dot, y_dot, theta_dot;	/*Shared resource*/
	private int n_players;
	
	/* near obstacles list */
	private int[] near_obst_heights;
	private int near_obst_start_i;
	
	//defaults to true and if we 
	private boolean gameOutcome;
	
	//constants
	private final int num_heights_in_near_list=4;
	private final int obstacle_width=5;
	
	public GameState(double[] position, double[] velocity, int nPlayers, int[] nearObstHeights, int nearObstIndex){
		if (position.length != 3)
		      throw new IllegalArgumentException("Positon array must be of length 3"); 
		if (velocity.length != 3)
		      throw new IllegalArgumentException("Velocity array must be of length 3"); 
		x=position[0];
		y=position[1];
		theta=position[2];
		
		x_dot = velocity[0];
		y_dot=velocity[1];
		theta_dot = velocity[2];
		n_players = nPlayers;
		near_obst_heights = nearObstHeights;
		near_obst_start_i = nearObstIndex;
		gameOutcome = true;	
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
	    x = Math.min(Math.max(x,0),100);	//calling 100= MAX_DOUBLE from SRS
	    y = Math.min(Math.max(y,0),10);
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
	synchronized int[] getNearObstList(){
		return near_obst_heights;
	}
	private void clampVelocity() {
	    x_dot = Math.min(Math.max(x_dot,0),10);	
	    y_dot = Math.min(Math.max(y_dot,0),10);
	    theta_dot = Math.min(Math.max(theta_dot, -Math.PI/2), Math.PI/2);
	}
	synchronized public double[] getVelocity(){
		double[] velocity = new double[3];
		velocity[0]=x_dot;
		velocity[1]=y_dot;
		velocity[2]=theta_dot;
		return velocity;
	}  
	private void setNumPlayers(int new_num_players){
		n_players= new_num_players;
	}
	private int getNumPlayers(){
		return n_players;
	}
	public int[] computeNearObstList(int[] allObstaclesList){
		int[] nearList = new int[num_heights_in_near_list];
		for (int i=near_obst_start_i;i<(num_heights_in_near_list+near_obst_start_i);i++){
			nearList[i-near_obst_start_i]=allObstaclesList[i];
		}
		return nearList;
	}
	public Path2D.Double getVehiclePosition(double x, double y){
		double vehicWidth=3;//Physics.length;
		double vehicHeight=3;// Physics.height;
		/*
		 * vehicle center is x,y
		 * setting 
		 */
		double[] topLeft = new double[2];
		double[] topRight = new double[2];
		double[] bottomLeft = new double[2];
		double[] bottomRight = new double[2];
		topLeft[0] = x-vehicWidth/2;
		topLeft[1] = y+vehicHeight/2;
		
		topRight[0]= x+vehicWidth/2;
		topRight[1] = y+vehicHeight/2;
		
		bottomLeft[0]=x-vehicWidth/2;
		bottomLeft[1]=y-vehicWidth/2;
		
		bottomRight[0]=x+vehicWidth/2;
		bottomRight[1]= y+vehicHeight/2;
		
		topLeft=computeCornerOfRect(topLeft[0], topLeft[1]);
		topRight=computeCornerOfRect(topRight[0], topRight[1]);
		bottomLeft=computeCornerOfRect(bottomLeft[0], bottomLeft[1]);
		bottomRight=computeCornerOfRect(bottomRight[0], bottomRight[1]);
		
		double[] xPoints=new double[4];
		xPoints[0]=topLeft[0];
		xPoints[1]=topRight[0];
		xPoints[2] =bottomLeft[0];
		xPoints[3] = bottomRight[0];
		
		double[] yPoints = new double[4];
		yPoints[0]=topLeft[1];
		yPoints[1]=topRight[1];
		yPoints[2] =bottomLeft[1];
		yPoints[3] = bottomRight[1];
		
		//Polygon currentVehic = new Polygon(xPoints, yPoints, 4);
		Path2D.Double currentVehic = new Path2D.Double();
		currentVehic.moveTo(topLeft[0], topLeft[1]);
		currentVehic.lineTo(topRight[0], topRight[1]);
		currentVehic.lineTo(bottomRight[0], bottomRight[1]);
		currentVehic.lineTo(bottomLeft[0], bottomLeft[1]);
		currentVehic.lineTo(topLeft[0], topLeft[1]);
		currentVehic.closePath();
		return currentVehic;
	}
	public boolean checkCollisions(){
		Path2D.Double vehicle = getVehiclePosition(x,y);
		
		for (int i=0; i<near_obst_heights.length;i++){
			Rectangle2D.Double rectangle = new Rectangle2D.Double(near_obst_start_i*5+i*5,near_obst_heights[i],1,near_obst_heights[i]);
			boolean result= testIntersection(vehicle, rectangle);
			if (result==false)
				gameOutcome=false;	//you lose.
		}
		return gameOutcome;
	}
	public double[] computeCornerOfRect(double xCorner, double yCorner){
		double sinTheta=Math.sin(theta);
		double cosTheta=Math.cos(theta);
		double newX=x+(xCorner-x)*cosTheta+(yCorner-y)*sinTheta;
		double newY = y-(xCorner-x)*sinTheta+(yCorner-y)*cosTheta;
		double[] cornerRect = new double[2];
		cornerRect[0]=newX;
		cornerRect[1]= newY;
		return cornerRect;
	}
	public static boolean testIntersection(Shape shape1, Shape shape2){
		Area a1 = new Area(shape1);
		a1.intersect(new Area(shape2));
		return a1.isEmpty();
	}
		
}
