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
	
	public static void main(String[] args) {		  

		try {			
			
			//initial the building
			Toolset.init();
			Toolset.DEBUG = false;
			
			//initial the building
	        Building building = Building.getBuilding();
	        building.initilize();
			
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
			
			/*
			//create the floor requests from person list
			ArrayList<Person> personList = building.getPersonList();			
			for (int i = 0; i < personList.size(); i++) {
				PersonImpl person = (PersonImpl) personList.get(i);
				
				Thread.sleep(person.getTriggerTime() - lastTriggerTime);
				lastTriggerTime = person.getTriggerTime();
				
				ElevatorImpl ele;
				switch(person.getPersonId())
				{
					case 1:
						ele = mapElevator.get("1");
						ele.addFloorRequest(person.getFromFloor(), DIRECTION.UP);
						break;
					case 2:
					case 3:
					case 4:
						ele = mapElevator.get("2");
						ele.addFloorRequest(person.getFromFloor(), DIRECTION.UP);
						break;
					case 5:
						ele = mapElevator.get("3");
						ele.addFloorRequest(person.getFromFloor(), DIRECTION.UP);
						break;						
				}												
	        }*/
			
			//create the rider requests from the panel list
			/*ArrayList<PanelRequest> panelRequestList = building.getPanelRequestList();
			for (int i = 0; i < panelRequestList.size(); i++) {
				PanelRequest panel = panelRequestList.get(i);
				
				Thread.sleep(panel.getTriggerTime() - lastTriggerTime);
				lastTriggerTime = panel.getTriggerTime();	
				
				ElevatorImpl ele = mapElevator.get(Integer.toString(panel.getElevatorId()));
				ele.addRiderRequest(panel.getFloorId());				
	        }			
			
			//wait for all done
			Thread.sleep(15000*2);
			
			//shut down all the elevators
			for (int i = 0; i < elevatorList.size(); i++) {
				ElevatorImpl ele = (ElevatorImpl) elevatorList.get(i);
				ele.halt();
	        }
			*/
			
			Thread.sleep(360*1000);
			Toolset.printReport(building.getFloorNumbers(), building.getElevatorNumbers(), building.getPersonList());
			Toolset.println("info", "Main thread exists.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
