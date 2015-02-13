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
		this.parentFloor = floor;
	}
	
	public void pressUp(Person person) {
		Controller.getInstance().addFloorRequest(person.getFromFloor(), DIRECTION.UP, person);
	}
	
	public void pressDown(Person person) {
		Controller.getInstance().addFloorRequest(person.getFromFloor(), DIRECTION.DOWN, person);
	}		
}
