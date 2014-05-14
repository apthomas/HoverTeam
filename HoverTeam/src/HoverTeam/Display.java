/**
 * HoverTeam


 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
package HoverTeam;

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
	/**
	 * The time in between redraws of the graphics [seconds]
	 */
	public final double updateTimeInterval=0.030;
	public double sF=75;	//scale Factor
	public double heightError=100;

	public Display(){
		System.out.println("completed constructor.");
	}
	public void paintComponent(Graphics g){
		System.out.println("currently painting.");
		Graphics2D g2 = (Graphics2D) g;
		/*
		 * Testing with random GameState
		 */
		double[] pos = {28,6,Math.PI/8};
		double[] vel = {5,5,0};
		int[] nearList = {4,8,7,5};
		GameState gs = new GameState(pos,vel,2,2,nearList,3);

		//GameState gs = GameClient.getGameState();
		/*
		 * Drawing the vehicle in the center of the screen with regards to the x-coordinate and then referencing the walls to it.
		 */
		Path2D.Double vehic = gs.getVehicleShapePath(frameWidth/2, gs.getPosition()[1]);
		g2.draw(vehic);
		int[] nearObstHeights = gs.getNearObstList();
		double vehiclePast = gs.getPosition()[0]%5;	//distance that the vehicle is past the second obstacle--reference to where to draw obstacles
		for (int i =0; i<nearObstHeights.length;i++){
			Rectangle2D.Double obstacle = new Rectangle2D.Double(frameWidth/2 -sF*vehiclePast+sF*5*(i-1),frameHeight-heightError,10,nearObstHeights[i]*sF);
			g2.draw(obstacle);
			g2.fill(obstacle);
		}
		score = gs.getPosition()[0]/5;
		g.drawString("Score:"+score, frameWidth/2, 100);
	}
	public void run(){
		/*
		 * No maximum score, game goes on forever.
		 */
		System.out.println("entered run method.");
		double t_start_abs = System.nanoTime()*1e-9;
		while (true){	
			double t_cycle_start = System.nanoTime()*1e-9 - t_start_abs;
			repaint();
			System.out.println("should have just repainted.");
			// Sleep until the next cycle
			double t_spent = (System.nanoTime()*1e-9 - t_start_abs) - t_cycle_start;
			double t_sleep = updateTimeInterval - t_spent;
			//System.out.println(String.format("t_spent=%.3fms", t_spent*1000));
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