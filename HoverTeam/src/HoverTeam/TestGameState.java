/**
 * HoverTeam
 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
package HoverTeam;

import static org.junit.Assert.*;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

/**
 * @author mvernacc
 *
 */
public class TestGameState {
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void test_checkCollisions_noCollision() {
		int[] nearObstHeights = {1};
		int nearObstIndex = 0;
		double[] pos = {5, 5, 0};
		double[] vel = {0, 0, 0};
		GameState state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);
		state.checkCollisions();
		assertTrue(state.getGameOutcome());
	}
	@Test
	public void test_checkCollisions_collision() {
		int[] nearObstHeights = {10};
		int nearObstIndex = 0;
		double[] pos = {0, 5, 0};
		double[] vel = {0, 0, 0};
		GameState state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);
		state.checkCollisions();
		assertFalse(state.getGameOutcome());
	}
	@Test
	public void test_checkCollisions_collision_floor() {
		int[] nearObstHeights = {1};
		int nearObstIndex = 0;
		double[] pos = {2, 0, 0};
		double[] vel = {0, 0, 0};
		GameState state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);
		state.checkCollisions();
		assertFalse(state.getGameOutcome());
	}
	public void test_checkCollisions_collision_ceiling() {
		int[] nearObstHeights = {1};
		int nearObstIndex = 0;
		double[] pos = {2, 10, 0};
		double[] vel = {0, 0, 0};
		GameState state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);
		state.checkCollisions();
		assertFalse(state.getGameOutcome());
	}
	@Test
	public void test_testInteresction_no_rect() {
		Rectangle2D.Double a = new Rectangle2D.Double(0,0,1,1);
		Rectangle2D.Double b = new Rectangle2D.Double(2,2,1,1);
		assertFalse(GameState.testIntersection(a, b));
	}
	@Test
	public void test_testInteresction_yes_rect() {
		Rectangle2D.Double a = new Rectangle2D.Double(0,0,1,1);
		Rectangle2D.Double b = new Rectangle2D.Double(-1,-1,1.5,1.5);
		assertTrue(GameState.testIntersection(a, b));
	}
	@Test
	public void test_testInteresction_no_path() {
		Rectangle2D.Double a = new Rectangle2D.Double(0,0,1,1);
		Path2D.Double p = new Path2D.Double();
		p.moveTo(1.5, 1.5);
		p.lineTo(1.5, 2.5);
		p.lineTo(2.5, 2.5);
		p.lineTo(2.5, 1.5);
		p.lineTo(1.5, 1.5);
		p.closePath();
		assertFalse(GameState.testIntersection(a, p));
	}
	@Test
	public void test_testInteresction_yes_path() {
		Rectangle2D.Double a = new Rectangle2D.Double(0,0,1,1);
		Path2D.Double p = new Path2D.Double();
		p.moveTo(0.5, 0.5);
		p.lineTo(0.5, 1.5);
		p.lineTo(1.5, 1.5);
		p.lineTo(1.5, 0.5);
		p.lineTo(0.5, 0.5);
		p.closePath();
		assertTrue(GameState.testIntersection(a, p));
	}
	@Test
	public void test_getVehicleShapePath() {
		int[] nearObstHeights = {10};
		int nearObstIndex = 0;
		double[] pos = {0, 5, 0};
		double[] vel = {0, 0, 0};
		GameState state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);
		Path2D.Double p = state.getVehicleShapePath(pos[0], pos[1],1);
		Rectangle2D bounds = p.getBounds2D();
		assertEquals(pos[0]-Physics.length/2, bounds.getMinX(), 1e-6);
		assertEquals(pos[0]+Physics.length/2, bounds.getMaxX(), 1e-6);
		assertEquals(pos[1]-Physics.height/2, bounds.getMinY(), 1e-6);
		assertEquals(pos[1]+Physics.height/2, bounds.getMaxY(), 1e-6);
	}
	@Test
	public void test_getVehicleShapePath_rotated() {
		int[] nearObstHeights = {10};
		int nearObstIndex = 0;
		double[] pos = {0, 5, Math.PI/2};
		double[] vel = {0, 0, 0};
		GameState state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);
		Path2D.Double p = state.getVehicleShapePath(pos[0], pos[1],1);
		Rectangle2D bounds = p.getBounds2D();
		assertEquals(pos[0]-Physics.height/2, bounds.getMinX(), 1e-6);
		assertEquals(pos[0]+Physics.height/2, bounds.getMaxX(), 1e-6);
		assertEquals(pos[1]-Physics.length/2, bounds.getMinY(), 1e-6);
		assertEquals(pos[1]+Physics.length/2, bounds.getMaxY(), 1e-6);
	}
	@Test
	public void test_getVehicleShapePath_intersect() {
		int[] nearObstHeights = {10};
		int nearObstIndex = 0;
		double[] pos = {0, 5, 0};
		double[] vel = {0, 0, 0};
		GameState state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);
		Path2D.Double p = state.getVehicleShapePath(pos[0], pos[1],1);
		Rectangle2D.Double a = new Rectangle2D.Double(0,0,1,10);
		assertTrue(GameState.testIntersection(p, a));
	}
}
