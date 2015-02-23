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
	
	/**
	 * Press the button on the floor call box. It will add a floor request to the elevator controller.
	 * @param direction - The request direction. 
	 */
	public void pressButton(DIRECTION direction) {
		Controller.getInstance().addFloorRequest(parentFloor.getFloorId(), direction);
	}	
}
