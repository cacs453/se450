package se450.elevator;

import java.util.ArrayList;

/**
 * 
 * @author Johnny
 *
 */
public class PanelRequest {

	private int elevatorid = 0;
	private int floorid = 0;
	private long triggertime = 0;
	
	public PanelRequest(int elevator, int floor, long trigger)
	{
		this.elevatorid = elevator;
		this.floorid = floor;
		this.triggertime = trigger;
	}
	
	public int getElevatorId() {
		return elevatorid;
	}    
   
    public int getFloorId() {
		return floorid;
	}
    
    public long getTriggerTime() {
		return triggertime;
	}
    
}
