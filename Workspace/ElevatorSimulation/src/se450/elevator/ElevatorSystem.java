package se450.elevator;

import java.util.ArrayList;
import java.util.HashMap;

import se450.elevator.common.DIRECTION;
import se450.elevator.common.Toolset;

/**
 * The simulator of the elevator system. This system reads the configuration from xml and runs as configured.
 * 
 */
public class ElevatorSystem {
	
	public static void main(String[] args) {		  

		try {			
			
			//initial the building
	        Building building = Building.getBuilding();
	        building.initilize();
        
			Toolset.init();
			Toolset.DEBUG = false;
			
			//create a hash map for elevator
			HashMap<String, ElevatorImpl> mapElevator = new HashMap<String, ElevatorImpl>();
			ArrayList<Elevator> elevatorList = building.getElevatorList();
			for (int i = 0; i < elevatorList.size(); i++) {
				ElevatorImpl ele = (ElevatorImpl) elevatorList.get(i);
				mapElevator.put(Integer.toString(ele.getElevatorID()), ele);
				ele.start();
	        }

			//create the floor requests from person list
			ArrayList<Person> personList = building.getPersonList();
			long lastTriggerTime = 0;
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
	        }
			
			//create the rider requests from the panel list
			ArrayList<PanelRequest> panelRequestList = building.getPanelRequestList();
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
			
			Toolset.println("info", "Main thread exists.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
