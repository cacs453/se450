package se450.elevator;

import java.security.InvalidParameterException;

import se450.elevator.common.DIRECTION;

/**
 * This interface is the abstraction for all Elevators
 * 
 * @author Shan Gao
 *
 */

public interface Elevator {
	/**
	 * This method is the interface for Controller to send a command to the elevator.
	 * Means someone pressed UP or DOWN button outside the elevator, then controller decided to ask this elevator to respond.
	 * 
	 * @param floor - Floor number in int format from 1 to MaxFloor.
	 * @param direction - Emu as in UP, DOWN. Can't be NONE here.
	 * @see #panelPressed(int)
	 */
	public void addFloorRequest (int floor, DIRECTION direction);
	
	/**
	 * This method is the interface for Person to send a command to the elevator.
	 * Means someone pressed a floor number button inside the elevator, then elevator will decided whether or not respond.
	 * 
	 * @param floorbutton - Floor number in int format from 1 to MaxFloor.
	 * @see #addFloorToTaskList(int)
	 */
	public void addRiderRequest (int floor);
	
	public void addRequest (Request request) throws InvalidParameterException;
}
