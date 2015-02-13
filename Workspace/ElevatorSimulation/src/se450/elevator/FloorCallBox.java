package se450.elevator;

import se450.elevator.common.DIRECTION;

/**
 * FloorCallBox
 * 
 * @author Cheng Zhang
 *
 */
public class FloorCallBox {	
	private Floor parentFloor;
	public FloorCallBox(Floor floor) {
		parentFloor = floor;
	}
	
	public void pressButton(DIRECTION direction, Person person) {
		Controller.getInstance().addFloorRequest(person.getFromFloor(), direction, person);
	}	
}
