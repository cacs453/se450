package se450.elevator;

import se450.elevator.common.DIRECTION;

/**
 * FloorCallBox
 * 
 * @author Cheng Zhang
 *
 */
public class FloorCallBox {	
	public FloorCallBox() {
	}
	
	public void pressUp(Person person) {
		Controller.getInstance().addFloorRequest(person.getFromFloor(), DIRECTION.UP, person);
	}
	
	public void pressDown(Person person) {
		Controller.getInstance().addFloorRequest(person.getFromFloor(), DIRECTION.DOWN, person);
	}		
}
