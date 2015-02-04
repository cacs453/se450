/**
 * @ Cheng Zhang
 * @ 
 */
package se450.elevator;

enum PERSON_STATUS 
{	NONE, 
	WAITING, 
	RIDDING, 
	ARRIVED
};

public interface Person {
	public int getFromFloor();
	public int getToFloor();
	public PERSON_STATUS getStatus();
	public void startWaiting();
	public void endWaiting();
	public void endRiding();
	public long getWaitTime();
	public long getRideTime();
}
