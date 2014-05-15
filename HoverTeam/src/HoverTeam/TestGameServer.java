package HoverTeam;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class TestGameServer {
	GameServer server;

	@Before
	public void setUp() throws Exception {
		server = new GameServer();
	}

	@Test
	public void test_generateNearList_in() {
		ArrayList<Integer> all_obst = new ArrayList<Integer>();
		for(int i=0; i<10; i++) {
			all_obst.add(i);
		}
		int i2 = 5;
		double x = GameState.obstacle_spacing*i2;
		int start_i = i2-GameState.num_heights_in_near_list/2 + 1;
		int[] near_list = server.generateNearList(all_obst, x);
		for(int i = 0; i<near_list.length; i++){
			assertEquals(all_obst.get(start_i+i), near_list[i], 1e-6);
			System.out.println(near_list[i]);
		}
	}
	@Test
	public void test_generateNearList_neg() {
		ArrayList<Integer> all_obst = new ArrayList<Integer>();
		for(int i=0; i<10; i++) {
			all_obst.add(i);
		}
		double x = -10;
		int[] near_list = server.generateNearList(all_obst, x);
		for(int i = 0; i<near_list.length; i++){
			assertEquals(all_obst.get(i), near_list[i], 1e-6);
		}
	}
	@Test
	public void test_generateNearList_edge() {
		ArrayList<Integer> all_obst = new ArrayList<Integer>();
		for(int i=0; i<10; i++) {
			all_obst.add(i);
		}
		int i2 = 10;
		double x = GameState.obstacle_spacing*i2;
		int start_i = i2-GameState.num_heights_in_near_list/2 + 1;
		int[] near_list = server.generateNearList(all_obst, x);
		System.out.print("Nearlist with veichle at edge:");
		for(int i = 0; i<near_list.length; i++){
			//assertEquals(all_obst.get(start_i+i), near_list[i], 1e-6);
			System.out.print(near_list[i] + " ");
		}
		System.out.println();
		assertEquals(all_obst.get(9), near_list[0], 1e-6);
	}

}
