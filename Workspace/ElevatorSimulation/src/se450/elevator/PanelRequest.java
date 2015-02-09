package se450.elevator;

import java.util.ArrayList;

/**
 * PanelRequest is a data instance which is used to store the requests info.
 * These requests are simulating, someone push the floor button inside the elevator
 * @author Rong Zhuang
 */
public class PanelRequest {

	private int elevatorid = 0;
	private int floorid = 0;
	private long triggertime = 0;
	
	/**
	 * Constructor of PanelRequest. Need to input default parameters of this PanelRequest instance;
	 * 
	 * @param elevatorid - The id of this elevator, int.
	 * @param floorid - The id of the floor where the person intends to go, int.
	 * @param triggertime - When the person push the floor button, long.
	 */
	public PanelRequest(int elevatorid, int floorid, long triggertime)
	{
		this.elevatorid = elevatorid;
		this.floorid = floorid;
		this.triggertime = triggertime;
	}
	
	/**
	 * Get the elevator id of the PanelRequest. 
	 * 
	 */
	public int getElevatorId() {
		return elevatorid;
	}    

	/**
	 * Get the floor id of the PanelRequest. 
	 * 
	 */

    public int getFloorId() {
		return floorid;
	}
    
	/**
	 * Get the trigger time of the PanelRequest. 
	 * 
	 */
    public long getTriggerTime() {
		return triggertime;
	}
    
}
