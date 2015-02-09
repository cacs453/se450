package se450.elevator;

import se450.elevator.common.DIRECTION;
import se450.elevator.common.REQUEST_TYPE;

/**
 * This class is the atomic element for each request including floor, rider and self request types.
 * @author Shan Gao
 *
 */
public class Request {
	public REQUEST_TYPE type;
	public int floor;
	public DIRECTION direction;
	
	public Request (REQUEST_TYPE type, int floor, DIRECTION direction) {
		this.type = type;
		this.floor = floor;
		this.direction = direction;
	}
}
