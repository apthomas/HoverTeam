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
	public void test_getThrusterPositions_5() {
		double[][] thruster_pos = Physics.getThrusterPositions(5);
		double[][] expected_pos = {{-0.5, -0.5}, {-0.25, -0.5}, {0, -0.5}, {0.25, -0.5}, {0.5, -0.5}};
		for(int i = 0; i<5; i++) {
			assertEquals(expected_pos[i][0], thruster_pos[i][0], 1e-6);
		}
	}
	@Test
	public void test_getThrusterPositions_1() {
		double[][] thruster_pos = Physics.getThrusterPositions(1);
		double[][] expected_pos = {{0, -0.5}};
		for(int i = 0; i<1; i++) {
			assertEquals(expected_pos[i][0], thruster_pos[i][0], 1e-6);
		}
	}
	@Test
	public void test_getThrusterPositions_2() {
		double[][] thruster_pos = Physics.getThrusterPositions(2);
		double[][] expected_pos = {{-0.5, -0.5}, {0.5, -0.5}};
		for(int i = 0; i<1; i++) {
			assertEquals(expected_pos[i][0], thruster_pos[i][0], 1e-6);
		}
	}
	
	@Test
	public void test_getForces() {
		boolean[] controls = {true};
		double[] expected_forces = {0, phys.F_thruster - phys.g*phys.m};
		GameState state = new GameState(zero_pos, zero_vel, 0.0, 1, obst, start_i);
		double[] forces = phys.getForces(controls, state);
		assertEquals(expected_forces[0], forces[0], 1e-6);
		assertEquals(expected_forces[1], forces[1], 1e-6);
	}
	@Test
	public void test_getForces_zero() {
		boolean[] controls = {false, false};
		double[] expected_forces = {0, - phys.g*phys.m};
		GameState state = new GameState(zero_pos, zero_vel, 0.0, 1, obst, start_i);
		double[] forces = phys.getForces(controls, state);
		assertEquals(expected_forces[0], forces[0], 1e-6);
		assertEquals(expected_forces[1], forces[1], 1e-6);
	}
	@Test
	public void test_getTorque_neg() {
		boolean[] controls = {true, false, false};
		double torque = phys.getTorque(controls);
		double expected_torque = -phys.F_thruster*(Physics.length/2);
		assertEquals(expected_torque, torque, 1e-6);
	}
	@Test
	public void test_getTorque_neg2() {
		boolean[] controls = {true, false};
		double torque = phys.getTorque(controls);
		double expected_torque = -phys.F_thruster*(Physics.length/2);
		assertEquals(expected_torque, torque, 1e-6);
	}
	@Test
	public void test_getTorque_pos() {
		boolean[] controls = {false, false, true};
		double torque = phys.getTorque(controls);
		double expected_torque = phys.F_thruster*(Physics.length/2);
		assertEquals(expected_torque, torque, 1e-6);
	}
	@Test
	public void test_getTorque_pos2() {
		boolean[] controls = {false, true};
		double torque = phys.getTorque(controls);
		double expected_torque = phys.F_thruster*(Physics.length/2);
		assertEquals(expected_torque, torque, 1e-6);
	}
	
	@Test
	public void test_getTorque_zero() {
		boolean[] controls = {true};
		double torque = phys.getTorque(controls);
		double expected_torque = 0;
		assertEquals(expected_torque, torque, 1e-6);
	}
	
	@Test
	public void test_getTorque_zero_multi() {
		boolean[] controls = {true, false, true};
		double torque = phys.getTorque(controls);
		double expected_torque = 0;
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
	/**
	 * Test that updateState obeys the conservation of angular  momentum.
	 */
	@Test
	public void test_updateState_ang_mom() {
		boolean[] controls = {true, false};
		double torque = -Physics.length/2 * phys.F_thruster;
		double t_final = 1e-2;
		
		GameState state = new GameState(zero_pos, zero_vel, 0.0, 1, obst, start_i);
		for(double t = 0; t<t_final; t += t_final*1e-4) {
			state.setTime(t);
			state = phys.updateState(state, controls);
		}
		double expected_ang_mom = torque*t_final;
		double actual_ang_mom = state.getVelocity()[2]*phys.I;
		assertEquals(expected_ang_mom, actual_ang_mom, 1e-5);
	}
	/**
	 * Test that updateState obeys the conservation of angular  momentum.
	 */
	@Test
	public void test_updateState_ang_mom_neg() {
		boolean[] controls = {false, true};
		double torque = Physics.length/2 * phys.F_thruster;
		double t_final = 1e-2;
		
		GameState state = new GameState(zero_pos, zero_vel, 0.0, 1, obst, start_i);
		for(double t = 0; t<t_final; t += t_final*1e-4) {
			state.setTime(t);
			state = phys.updateState(state, controls);
		}
		double expected_ang_mom = torque*t_final;
		double actual_ang_mom = state.getVelocity()[2]*phys.I;
		assertEquals(expected_ang_mom, actual_ang_mom, 1e-5);
	}
	/**
	 * Test that updateState obeys the conservation of linear momentum in the x direction.
	 */
	@Test
	public void test_updateState_lin_mom() {
		boolean[] controls = {true};
		double[] forces = {-phys.F_thruster, - phys.m * phys.g};
		double t_final = 1;
		double[] tilted_pos = {0,5,Math.PI/2};
		
		GameState state = new GameState(tilted_pos, zero_vel, 0.0, 1, obst, start_i);
		for(double t = 0; t<t_final; t += t_final*1e-4) {
			state.setTime(t);
			state = phys.updateState(state, controls);
		}
		double[] expected_lin_mom = {forces[0]*t_final, forces[1]*t_final};
		double[] actual_lin_mom = {
				state.getVelocity()[0]*phys.m,
				state.getVelocity()[1]*phys.m
		};
		assertEquals(expected_lin_mom[0], actual_lin_mom[0], 1e-5);
		assertEquals(expected_lin_mom[1], actual_lin_mom[1], 1e-5);
	}
}
