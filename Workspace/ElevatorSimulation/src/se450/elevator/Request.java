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
	
	public static Request createWithPerson(Person person) {
		Request r = new Request(REQUEST_TYPE.FLOOR, person.getFromFloor(), person.direction());		
		return r;
	}
	
	/**
	 * Check if this is equal to the input request.
	 * @param request
	 * @return
	 */
	public boolean isEqualTo(Request request) {
		boolean result = false;
		if (this.type == request.type && this.floor==request.floor && this.direction==request.direction)
			result = true;
		return result;
	}
	
	public String toInfoString() {
		String str = String.format("%d%s", this.floor, this.direction==DIRECTION.UP ? "¡ü" : "¡ý");
		return str;
	}
}
