package HoverTeam;
/**
 * HoverTeam


 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Display extends JPanel implements Runnable, KeyListener{
	/**
	 * The size of the display window.
	 * @see Req. 3.2.4.1.1
	 */
	public static final int frameWidth=500;
	/**
	 * The size of the display window.
	 * @see Req. 3.2.4.1.1
	 */
	public static final int frameHeight=300;
	public double score;
	/**
	 * The time in between redraws of the graphics [seconds].
	 */
	public final double updateTimeInterval=0.030;
	/**
	 * The scaling factor for the graphics.
	 * @see Req. 3.2.4.1.1
	 */
	public static double sF = frameHeight/10.5;	//scale Factor
	public double heightError=100;
	protected String myHostName;
	/**
	 * The GameClient associated with this display.
	 * @see Req 3.2.4.1.3
	 */
	GameClient gc;
	
	/**
	 * Construtor
	 * @see Req. 3.2.4.2
	 */
	public Display(){

	}

	public Display(String hostname){
		myHostName=hostname;
	}
	
	/**
	 * Redraw the graphics
	 * @see Req. 3.2.4.3
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		/*
		 * Testing with random GameState
		 */
		double[] pos = {47,9,Math.PI/8};
		double[] vel = {5,5,0};
		int[] nearList = {4,8,7,5};
		GameState gs = new GameState(pos,vel,2,7,nearList,3);
		if(gc != null && gc.getState() != null) {
			gs = gc.getState();
		}
		/*
		 * Drawing the vehicle in the center of the screen with regards to the x-coordinate and then referencing the walls to it.
		 */
		g2.setColor(Color.red);
		Path2D.Double vehic = gs.getVehicleShapePath(frameWidth/2, gs.getPosition()[1]*sF,(int)sF);
		AffineTransform mirror = AffineTransform.getScaleInstance(1.0,-1.0);
		mirror.translate(0, -frameHeight);
		Path2D.Double transformedVehic = new Path2D.Double(vehic, mirror);
		g2.draw(transformedVehic);
		g2.fill(transformedVehic);

		g2.setColor(Color.blue);
		Path2D.Double bottomLine = gs.getVehicleBottomLine(frameWidth/2, gs.getPosition()[1]*sF,(int)sF);
		Path2D.Double transformedLine = new Path2D.Double(bottomLine, mirror);
		g2.draw(transformedLine);
		g2.fill(transformedLine);

		int[] nearObstHeights = gs.getNearObstList();
		double vehiclePast = gs.getPosition()[0]%5;	//distance that the vehicle is past the second obstacle--reference to where to draw obstacles
		g2.setColor(Color.black);
		for (int i =0; i<nearObstHeights.length;i++){
			Rectangle2D.Double obstacle = new Rectangle2D.Double(frameWidth/2 -sF*vehiclePast+sF*5*(i-1),0 ,sF,nearObstHeights[i]*sF);
			//frameHeight-nearObstHeights[i]*sF
			Path2D.Double transformedObstacle = new Path2D.Double(obstacle, mirror);
			g2.draw(transformedObstacle);
			g2.fill(transformedObstacle);
		}
		g2.setColor(Color.green);
		Path2D.Double[] thrusters = gs.getThrusterLocations(frameWidth/2, gs.getPosition()[1]*sF,(int)sF);
		if(thrusters != null) {
			for (int j = 0;j<thrusters.length;j++){
				Path2D.Double thruster = thrusters[j];
				Path2D.Double transformedThruster = new Path2D.Double(thruster,mirror);
				g2.draw(transformedThruster);
				g2.fill(transformedThruster);

			}
		}
		g2.setColor(Color.black);
		score = Math.floor(gs.getPosition()[0]/5);
		g.drawString("Score is:"+score, frameWidth/2, (int)heightError-50);
		g.drawString("KEY", 20, 15);
		g.drawString("Black denotes obstacle", 20, 30);
		g.drawString("Red denotes vehicle", 20, 45);
		g.drawString("Blue denotes bottom side of vehicle", 20, 60);
		g.drawString("Green denotes thrusters",20,75);
		g.drawString("Display running on "+myHostName, frameWidth-300,30);
	}
	
	/**
	 * Associate a GmaeClient with this display.
	 * @see Req. 3.2.4.4
	 */
	public void setGameClient(GameClient gc){
		this.gc=gc;
	}
	
	/**
	 * Execute the Display thread.
	 * @see Req. 3.2.4.5
	 */
	public void run(){
		/*
		 * No maximum score, game goes on forever.
		 */
		System.out.println("entered run method.");
		double t_start_abs = System.nanoTime()*1e-9;
		while (true){	
			double t_cycle_start = System.nanoTime()*1e-9 - t_start_abs;
			repaint();
			//System.out.println("should have just repainted.");
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
	
	/** Set up the graphics frame and labels
	 * @see Req 3.2.4.8
	 */
	public void setup() {
		JFrame frame = new JFrame("Frame and Panel");
		JLabel label= new JLabel("HoverTeam by Aaron Thomas and Matthew Vernacchia");
		frame.setTitle("You are playing HoverTeam!!!");
		this.add(label);
		frame.getContentPane().add(this);
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);
		/* Add this thread as a KeyListener so that the KeyPressed and KeyReleased callbacks
		 * will be called when the user touches keys.
		 */
		frame.addKeyListener(this);
	}
	
	public static void main(String[] args){
		/*
		JFrame frame = new JFrame("Frame and Panel");
		JLabel label= new JLabel("HoverTeam by Aaron Thomas and Matthew Vernacchia");
		frame.setTitle("You are playing HoverTeam!!!");
		Display panel = new Display();
		(new Thread(panel)).start();
		panel.add(label);
		frame.getContentPane().add(panel);
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);
		 */
		try {
			ServerSocket s = new ServerSocket(5065);
			s.setReuseAddress(true);      
			if (!s.isBound())
				System.exit(-1);
			String address = GeneralInetAddress.getLocalHost().getHostAddress();
			Display panel = new Display(address);
			panel.setup();
			(new Thread(panel)).start();
			/*
		      do {
			Socket client = s.accept();
			.addClient(client);
		      } while (true);
			 */
		} 
		catch (IOException e) {
			System.err.println("I couldn't create a new socket.\n"+
					"You probably are already running DisplayServer.\n");
			System.err.println(e);
			System.exit(-1);
		}

	}

	/**
	 * Turn on the thruster when the space bar is pressed.
	 * @see Req 3.2.4.6
	 * */
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		//System.out.println("key pressed");
		// If the space bar was pressed...
		if(keyCode == 32) {
			//...turn the thruster on.
			if(gc != null) { gc.thrusterOn(); }
		}
	}

	/**
	 * Turn off the thruster when the space bar is released.
	 * @see Req 3.2.4.7
	 * */
	@Override
	public void keyReleased(KeyEvent e) {
		//System.out.println("key released");
		int keyCode = e.getKeyCode();
		// If the space bar was released...
		if(keyCode == 32) {
			//...turn the thruster off.
			if(gc != null) { gc.thrusterOff(); }
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub		
	}
}
