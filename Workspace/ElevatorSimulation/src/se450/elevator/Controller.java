package se450.elevator;

import java.util.ArrayList;

import se450.elevator.common.*;
import se450.elevator.Request;

/**
 * The Elevator Controller will take care all the floor requests and dispatch them to each elevator depending on their running status
 * This class hasn't been implemented for Phase II.
 * 
 * @author Shan Gao
 *
 */
public class Controller extends Thread {
	private static Controller controller = new Controller();
	private ArrayList<Request> pendingList = new ArrayList<Request>();
	
	public static Controller getInstance() {
		return controller;
	}
	
	public void addFloorRequest (int floor, DIRECTION direction) {
		Request request = new Request(REQUEST_TYPE.FLOOR, floor, direction);
		
		// Check which elevator can response to the request.
		ArrayList<Elevator> elevatorList = Building.getBuilding().getElevatorList();
		
		
	}
}
