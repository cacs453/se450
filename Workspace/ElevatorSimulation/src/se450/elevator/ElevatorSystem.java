package se450.elevator;

import java.util.ArrayList;
import java.util.HashMap;

import se450.elevator.common.DIRECTION;
import se450.elevator.common.Toolset;

/**
 * The simulator of the elevator system. This system reads the configuration from xml and runs as configured.
 * @author Rong Zhuang
 */
public class ElevatorSystem {
	public final static Object eventLock = new Object(); //Lock object in main thread waiting for all tasks being fulfilled.
	
	/**
	 * Stop all threads and output logs.
	 */
	public static void onAllTasksFulfilled() {	
		ArrayList<Elevator> elevatorList = Building.getBuilding().getElevatorList();
		synchronized(elevatorList) {
			for (Elevator ele : elevatorList) {
				((ElevatorImpl)ele).halt();
	        }
		}			
		Controller.getInstance().halt();
		Toolset.println("info", "All tasks have been fulfilled, all threads have been stopped.");		
	}
	
	public static void main(String[] args) {		  
		try {						
			//initial the building
			Toolset.init();
			Toolset.DEBUG = false;
			
			//initial the building
	        Building building = Building.getBuilding();
	        building.initilize();			
	        
	        //Set the lock for main thread to ElevatorController.
	        Controller.getInstance().setMainThreadLock(eventLock);
	        
			//create a hash map for elevator
			HashMap<String, ElevatorImpl> mapElevator = new HashMap<String, ElevatorImpl>();
			ArrayList<Elevator> elevatorList = building.getElevatorList();
			for (int i = 0; i < elevatorList.size(); i++) {
				ElevatorImpl ele = (ElevatorImpl) elevatorList.get(i);
				mapElevator.put(Integer.toString(ele.getElevatorID()), ele);
				ele.start();
	        }
			
			//start elevator system
			building.getElevatorController().start();			

			long lastTriggerTime = 0;
			
			//create person randomly
			PersonGenerator pg = new PersonGenerator(building.getFloorNumbers(), building.getRandomPersonNumbers(), building.getSimulationDuration());
			pg.setFloorList(building.getFloorsList());
			building.setPersonList(pg.getPersonList()); // Add the reference of the person list to building
			new Thread(pg).start();
			
			//Wait until all tasks are fulfilled.
			synchronized(eventLock) {
				eventLock.wait();
			}
			
			//Received the notification that all tasks have been fulfilled. 
			onAllTasksFulfilled();
			
			Toolset.printReport(building.getFloorNumbers(), building.getElevatorNumbers(), building.getPersonList());
			Toolset.println("info", "Main thread exists.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
