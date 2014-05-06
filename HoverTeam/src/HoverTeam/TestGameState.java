/**
 * HoverTeam
 * 16.35 Spring 2014 Final Project
 * @author Aaron Thomas and Matt Vernacchia
 */
package HoverTeam;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author mvernacc
 *
 */
public class TestGameState {
	GameState state;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test_checkCollisions_noCollision() {
		int[] nearObstHeights = {1,1,1,1};
		int nearObstIndex = 0;
		double[] pos = {5, 5, 0};
		double[] vel = {0, 0, 0};
		state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);
		state.checkCollisions();
		assertTrue(state.getGameOutcome());
	}
	@Test
	public void test_checkCollisions_collision() {
		int[] nearObstHeights = {10};
		int nearObstIndex = 0;
		double[] pos = {0, 5, 0};
		double[] vel = {0, 0, 0};
		state = new GameState(pos, vel, 0, 0, nearObstHeights, nearObstIndex);
		state.checkCollisions();
		assertFalse(state.getGameOutcome());
	}

}
