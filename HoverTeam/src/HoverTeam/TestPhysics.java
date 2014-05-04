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
public class TestPhysics {
	Physics phys;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		phys = new Physics();
	}
	
	@Test
	public void test_getThrusterPositions() {
		double[][] thruster_pos = Physics.getThrusterPositions(5);
		double[][] expected_pos = {{-0.5, -0.5}, {-0.25, -0.5}, {0, -0.5}, {0.25, -0.5}, {0.5, -0.5}};
		for(int i = 0; i<5; i++) {
			assertEquals(expected_pos[i][0], thruster_pos[i][0], 1e-6);
		}
	}
	
	@Test
	public void test_getForces() {
		boolean[] controls = {true};
		double[] expected_forces = {0, phys.F_thruster - phys.g*phys.m};
		double[] forces = phys.getForces(controls);
		assertEquals(expected_forces[0], forces[0], 1e-6);
		assertEquals(expected_forces[1], forces[1], 1e-6);
	}
	@Test
	public void test_getTorque() {
		boolean[] controls = {true, false, false};
		double torque = phys.getTorque(controls);
		double expected_torque = -phys.F_thruster*(Physics.length/2);
		assertEquals(expected_torque, torque, 1e-6);
	}

}
