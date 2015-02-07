package se450.elevator;

import java.util.ArrayList;
import java.util.HashMap;

import se450.elevator.common.Toolset;

/**
 * 
 * @author Johnny
 *
 */
public class ElevatorSystem {
	
	public static void main(String[] args) {
		
		HashMap<String, ElevatorImpl> mapElevator = new HashMap<String, ElevatorImpl>();  

		try {
	        Building building = Building.getBuilding();
	        building.initilize();
        
			Toolset.init();
			Toolset.DEBUG = false;
			
			ArrayList<Elevator> elevatorList = building.getElevatorList();
			for (int i = 0; i < elevatorList.size(); i++) {
				ElevatorImpl ele = (ElevatorImpl) elevatorList.get(i);
				mapElevator.put(Integer.toString(ele.getElevatorID()), ele);
				ele.start();
	        }
			/*ElevatorImpl e1 = ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
			ElevatorImpl e2 = ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
			ElevatorImpl e3 = ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
			e1.start();
			e2.start();
			e3.start();*/
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
			/*e1.addFloorRequest(11, DIRECTION.UP);
			Thread.sleep(600);
			e2.addFloorRequest(14, DIRECTION.UP);
			Thread.sleep(600);
			e2.addFloorRequest(13, DIRECTION.UP);
			Thread.sleep(600);
			e2.addFloorRequest(15, DIRECTION.UP);
			Thread.sleep(35000);
			e3.addFloorRequest(5, DIRECTION.UP);
			Thread.sleep(500*7);*/
			
			ArrayList<PanelRequest> panelRequestList = building.getPanelRequestList();
			for (int i = 0; i < panelRequestList.size(); i++) {
				PanelRequest panel = panelRequestList.get(i);
				
				Thread.sleep(panel.getTriggerTime() - lastTriggerTime);
				lastTriggerTime = panel.getTriggerTime();	
				
				ElevatorImpl ele = mapElevator.get(Integer.toString(panel.getElevatorId()));
				ele.addRiderRequest(panel.getFloorId());				
	        }			
			/*e3.addRiderRequest(16);
			Thread.sleep(500);
			e3.addRiderRequest(1);
			Thread.sleep(500*16);
			e3.addRiderRequest(2);
			Thread.sleep(500);
			e3.addRiderRequest(5);
			Thread.sleep(500);
			e3.addRiderRequest(3);*/

			Thread.sleep(15000*2);
			for (int i = 0; i < elevatorList.size(); i++) {
				ElevatorImpl ele = (ElevatorImpl) elevatorList.get(i);
				ele.halt();
	        }
			/*Thread.sleep(15000*2);
			e1.halt();
			e2.halt();
			e3.halt();*/
			
			Toolset.println("info", "Main thread exists.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
