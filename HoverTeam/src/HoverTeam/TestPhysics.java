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
	double[] zero_vel = {0,0,0};
	double[] zero_pos = {0,0,0};
	int[] obst = {0};
	int start_i = 0;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		phys = new Physics(null);

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
	
	@Test
	public void test_dynamics_force_hover() {
		double[] forces = {0, phys.m*phys.g};
		double torque = 0;
		
		GameState state = new GameState(zero_pos, zero_vel, 0.0, 1, obst, start_i);
		GameState new_state = phys.dynamics(state, forces, torque);
		
		assertEquals(0, new_state.getVelocity()[0], 1e-6);
		assertEquals(0, new_state.getVelocity()[1], 1e-6);
		assertEquals(0, new_state.getVelocity()[2], 1e-6);
		
		assertEquals(0, new_state.getPosition()[0], 1e-6);
		assertEquals(0, new_state.getPosition()[1], 1e-6);
		assertEquals(0, new_state.getPosition()[2], 1e-6);
	}
	/**
	 * Test that the dynamics obeys the conservation of angular  momentum.
	 */
	@Test
	public void test_dynamics_ang_mom() {
		double[] forces = {0, 0};
		double torque = 1e-2;
		double t_final = 1;
		
		GameState state = new GameState(zero_pos, zero_vel, 0.0, 1, obst, start_i);
		for(double t = 0; t<t_final; t += t_final*1e-4) {
			state.setTime(t);
			state = phys.dynamics(state, forces, torque);
		}
		double expected_ang_mom = torque*t_final;
		double actual_ang_mom = state.getVelocity()[2]*phys.I;
		assertEquals(expected_ang_mom, actual_ang_mom, 1e-5);
	}
	/**
	 * Test that the dynamics obeys the conservation of linear momentum in the x direction.
	 */
	@Test
	public void test_dynamics_lin_mom() {
		double[] forces = {1e-2, 0};
		double torque = 0;
		double t_final = 1;
		
		GameState state = new GameState(zero_pos, zero_vel, 0.0, 1, obst, start_i);
		for(double t = 0; t<t_final; t += t_final*1e-4) {
			state.setTime(t);
			state = phys.dynamics(state, forces, torque);
		}
		double expected_lin_mom = forces[0]*t_final;
		double actual_lin_mom = state.getVelocity()[0]*phys.m;
		assertEquals(expected_lin_mom, actual_lin_mom, 1e-5);
	}
}
