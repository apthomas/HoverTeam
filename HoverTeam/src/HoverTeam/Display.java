/**
 * HoverTeam
 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
package HoverTeam;

/**
 * HoverTeam


 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
public class Display extends JFrame implements Runnable{
	public static final int frameWidth=30;
	public static final int frameHeight=30;
	public int sec;
	public int mSec;
	public double score;
	public double updateTimeInterval=25;
	public double prevUpdatedTime=0;
	
	public Display(){
		JFrame frame = new JFrame();
		frame.setSize(frameWidth, frameHeight);
		frame.setTitle("You are playing HoverTeam!!!");
		sec=0;
		mSec=0;
	}
	public void repaint(){
		GameState gs = GameClient.getGameState();
		Graphics2D g = (Graphics2D)this.getGraphics();
		/*
		 * Drawing the vehicle in the center of the screen with regards to the x-coordinate and then referencing the walls to it.
		 */
		Path2D.Double vehic = gs.getVehicleShapePath(frameWidth/2, gs.getPosition()[1]);
		g.draw(vehic);
		int[] nearObstHeights = gs.getNearObstList();
		double vehiclePast = gs.getPosition()[0]%5;	//distance that the vehicle is past the second obstacle--reference to where to draw obstacles
		for (int i =0; i<nearObstHeights.length;i++){
			Rectangle2D.Double obstacle = new Rectangle2D.Double(frameWidth/2 -vehiclePast+5*(i-1),nearObstHeights[i],1,nearObstHeights[i]);
			g.draw(obstacle);
		}
		score = gs.getPosition()[0]/5;
		g.drawString("Score:"+score, frameWidth/2, frameHeight);
		
	}
	public void run(){
		double tAdvanced=0;
	
		while (score<25){
			long currentTime = System.currentTimeMillis();
	    	if (currentTime-prevUpdatedTime>updateTimeInterval){
	    		prevUpdatedTime = currentTime;
	    		repaint();
	    	}
		}
	    System.out.println("You win!!!");
		
	}
}