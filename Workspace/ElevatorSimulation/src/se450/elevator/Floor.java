package se450.elevator;

import java.util.ArrayList;

import se450.elevator.common.DIRECTION;

/**
 * The interface for all the floors 
 */
public interface Floor {

	/**
	 * Add the persons to this floor during the building initialization.
	 * @param person
	 */
	public void addPerson (Person person);
	
	/**
	 * Remove the person from this floor if he/she gets in the elevator
	 * @param personList
	 */
	public void removePerson (ArrayList<Person> personList);
	
	/**
	 * Send a request to Controller with the direction.
	 * Means someone pressed a up/down button in this floor, then controller should send an elevator to this floor.
	 * @param direction
	 */
	public void pressButton (DIRECTION direction);
	
	/**
	 * Get the list of persons who are waiting for the elevator in this floor
	 * @return The list waiting person 
	 */
	public ArrayList<Person> getWaitingList();
	
	/**
	 * Get the floorId
	 * @return floorId
	 */
	public int getFloorId();
}
