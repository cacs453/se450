/**
 * Person class.
 * 
 * @author Cheng Zhang
 *
 */
package se450.elevator;

import se450.elevator.common.DIRECTION;
import se450.elevator.common.PERSON_STATUS;

public interface Person {
	/**
	 * Get person id.
	 * @return Person id.
	 */
	public int getPersonId();
	
	/**
	 * Get from floor.
	 * @return From floor
	 */
	public int getFromFloor();
	/**
	 * Get to floor.
	 * @return To floor
	 */
	public int getToFloor();
	
	/**
	 * Get status of the person.
	 * @return Status
	 */
	public PERSON_STATUS getStatus();
		
	/**
	 * The preset time when the person will trigger the elevator button.
	 * @return Preset triggerTime.
	 */
	public long getTriggerTime(); 
	
	/**
	 * Start waiting.
	 */
	public void startWaiting();
	/**
	 * End waiting.	 
	 */
	public void endWaiting();
	
	/**
	 * End riding.
	 */
	public void endRiding();
	/**
	 * Get wait time.
	 */	
	public long getWaitTime();
	/**
	 * Get ride time.
	 */		
	public long getRideTime();
	
	public DIRECTION direction();
	
}
