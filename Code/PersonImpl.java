/**
 * @ Cheng Zhang
 * @ 
 */

public class PersonImpl implements Person {
	private int id;
	private int fromFloor;
	private int toFloor;
	PERSON_STATUS status; 
	private long waitStartTime; 
	private long waitEndTime;
	private long rideEndTime;	
	
	public PersonImpl(int id, int fromFloor, int toFloor) {
		this.id = id;
		this.fromFloor = fromFloor;
		this.toFloor = toFloor;		
		this.status = PERSON_STATUS.NONE;		
	}
	
	public int getFromFloor() {
		return fromFloor;
	}
	
	public int getToFloor() {
		return toFloor;
	}
	
	public PERSON_STATUS getStatus() {
		return status;
	}
	
	public void startWaiting() {
		this.waitStartTime = System.currentTimeMillis();
		this.status = PERSON_STATUS.WAITING;
	}
	
	public void endWaiting() {
		this.waitEndTime = System.currentTimeMillis();
		this.status = PERSON_STATUS.RIDDING;
	}
	
	public void endRiding() {
		this.rideEndTime = System.currentTimeMillis();
		this.status = PERSON_STATUS.ARRIVED;
	}
	
	public long getWaitTime() {
		return waitEndTime - waitStartTime;
	}
	
	public long getRideTime() {
		return rideEndTime - waitEndTime;
	}
}


