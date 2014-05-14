package HoverTeam;
/**
 * HoverTeam


 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
public class Display extends JPanel implements Runnable{
	public static final int frameWidth=1300;
	public static final int frameHeight=700;
	public double score;
	public double updateTimeInterval=25;
	public double prevUpdatedTime=0;
	public double sF=60;	//scale Factor
	public double heightError=100;
	
	public Display(){
		
	}
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		/*
		 * Testing with random GameState
		
		double[] pos = {43,10,Math.PI/4};
		double[] vel = {5,5,0};
		int[] nearList = {4,8,7,5};
		GameState gs = new GameState(pos,vel,2,2,nearList,3);
		*/
		GameState gs = GameClient.getState();
		/*
		 * Drawing the vehicle in the center of the screen with regards to the x-coordinate and then referencing the walls to it.
		 */
		g2.setColor(Color.red);
		Path2D.Double vehic = gs.getVehicleShapePath(frameWidth/2, gs.getPosition()[1]*sF,(int)sF);
		g2.draw(vehic);
		g2.fill(vehic);
		int[] nearObstHeights = gs.getNearObstList();
		double vehiclePast = gs.getPosition()[0]%5;	//distance that the vehicle is past the second obstacle--reference to where to draw obstacles
		g2.setColor(Color.black);
		for (int i =0; i<nearObstHeights.length;i++){
			Rectangle2D.Double obstacle = new Rectangle2D.Double(frameWidth/2 -sF*vehiclePast+sF*5*(i-1),frameHeight-nearObstHeights[i]*sF ,sF,nearObstHeights[i]*sF);
			g2.draw(obstacle);
			g2.fill(obstacle);
		}
		//frameHeight-heightError
		score = Math.floor(gs.getPosition()[0]/5);
		g.drawString("Score is:"+score, frameWidth/2, (int)heightError-50);
	}
	public void run(){
		/*
		 * No maximum score, game goes on forever.
		 */
		while (true){	
			long currentTime = System.currentTimeMillis();
	    	if (currentTime-prevUpdatedTime>updateTimeInterval){
	    		prevUpdatedTime = currentTime;
	    		repaint();
	    	}
		}
		
	}
	public static void main(String[] args){
		JFrame frame = new JFrame("Frame and Panel");
		JLabel label= new JLabel("HoverTeam by Aaron Thomas and Matthew Vernacchia");
		frame.setTitle("You are playing HoverTeam!!!");
		Display panel = new Display();
		(new Thread(panel)).start();
		panel.add(label);
		frame.getContentPane().add(panel);
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);
	   
	}
}