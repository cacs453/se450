/**
 * @ Cheng Zhang
 * @ 
 */
package se450.elevator;
import se450.elevator.common.PERSON_STATUS;
import se450.elevator.common.*;

/**
 * Person implementation.
 * 
 * @author Cheng Zhang
 *
 */
public class PersonImpl implements Person {
	private int id;
	private int elevatorId; // The elevator id which the person is taking.	
	private int fromFloor;
	private int toFloor;
	private long triggerTime; 
	PERSON_STATUS status; 
	private long waitStartTime; 
	private long waitEndTime;
	private long rideEndTime;	
			
	/**
	 * Constructor function.
	 * @param id - The person id.
	 * @param fromFloor - The floor where the person was from.
	 * @param toFloor - The floor where the person will go to.
	 * @param triggerTime - The preset time when the person will trigger the elevator button.
	 */
	public PersonImpl(int id, int fromFloor, int toFloor, long triggerTime) {
		this.id = id;
		this.elevatorId = -1;
		this.fromFloor = fromFloor;
		this.toFloor = toFloor;
		this.triggerTime = triggerTime;
		this.status = PERSON_STATUS.NONE;		
	}
		
	public int getElevatorId() {
		return elevatorId;
	}
	
	public int getPersonId() {
		return id;
	}
	
	public int getFromFloor() {
		return fromFloor;
	}
	
	public int getToFloor() {
		return toFloor;
	}
	
	public long getTriggerTime() {
		return triggerTime;
	}
	
	public PERSON_STATUS getStatus() {
		return status;
	}
	
	/**
	 * Switch to waiting status and set waitStartTime.
	 */
	public void startWaiting() {
		this.waitStartTime = Toolset.getDeltaTimeLong();
		this.status = PERSON_STATUS.WAITING;
	}
	
	/**
	 * End waiting status and set waitEndTime.
	 */
	public void endWaiting() {
		this.waitEndTime = Toolset.getDeltaTimeLong();
		this.status = PERSON_STATUS.RIDDING;
	}
	
	/**
	 * End riding status and set rideEndTime.
	 */
	public void endRiding() {
		this.rideEndTime = Toolset.getDeltaTimeLong();
		this.status = PERSON_STATUS.ARRIVED;
	}
	
	public long getWaitTime() {
		return waitEndTime - waitStartTime;
	}
	
	public long getRideTime() {
		return rideEndTime - waitEndTime;
	}
	
	/**
	 * Calculate the direction by the request of the person.
	 */
	public DIRECTION direction() {
		DIRECTION direction;
		if (this.toFloor == this.fromFloor) 
			direction = DIRECTION.NONE;
		else {
			direction = this.toFloor > this.fromFloor ? DIRECTION.UP : DIRECTION.DOWN;
		}
		return direction;
	}
}


