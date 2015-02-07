package se450.elevator;

public class Request {
	public REQUEST_TYPE type;
	public int floor;
	public DIRECTION direction;
	
	Request (REQUEST_TYPE type, int floor, DIRECTION direction) {
		this.type = type;
		this.floor = floor;
		this.direction = direction;
	}
}
