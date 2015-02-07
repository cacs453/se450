package se450.elevator;

import java.util.ArrayList;

import se450.elevator.common.Toolset;

public class Tester {

	public static void main(String[] args) {
		try {
			Toolset.init();
			Toolset.DEBUG = false;
			ElevatorImpl e1 = ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
			ElevatorImpl e2 = ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
			ElevatorImpl e3 = ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
			e1.start();
			e2.start();
			e3.start();
			e1.addFloorRequest(11, DIRECTION.UP);
			Thread.sleep(600);
			e2.addFloorRequest(14, DIRECTION.UP);
			Thread.sleep(600);
			e2.addFloorRequest(13, DIRECTION.UP);
			Thread.sleep(600);
			e2.addFloorRequest(15, DIRECTION.UP);
			Thread.sleep(35000);
			e3.addFloorRequest(5, DIRECTION.UP);
			Thread.sleep(500*7);
			e3.addRiderRequest(16);
			Thread.sleep(500);
			e3.addRiderRequest(1);
			Thread.sleep(500*16);
			e3.addRiderRequest(2);
			Thread.sleep(500);
			e3.addRiderRequest(5);
			Thread.sleep(500);
			e3.addRiderRequest(3);

			Thread.sleep(15000*2);
			e1.halt();
			e2.halt();
			e3.halt();
			
			Toolset.println("info", "Main thread exists.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
