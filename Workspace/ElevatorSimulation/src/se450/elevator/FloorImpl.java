package se450.elevator;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import se450.elevator.common.DIRECTION;
import se450.elevator.common.PERSON_STATUS;

/**
 * The implementation for the floor interface. 
 */
public class FloorImpl implements Floor {
	
	private int floorId = 0;
	private FloorCallBox callbox = new FloorCallBox(this);
	private ArrayList<Person> personList = new ArrayList<Person>();
	
	/**
	 * Constructor of FloorImpl. 
	 * @param floorId - The Id of the floor
	 * @throws InvalidParameterException
	 */
	public FloorImpl(int floorId) throws InvalidParameterException {

		if (floorId<=0 )
			throw new InvalidParameterException("Floor Id must be greater than zero.");
		
		this.floorId = floorId;
	}
	
	/**
	 * Get the floorId
	 * @return floorId
	 */
	public int getFloorId() {
		return this.floorId;
	}	
	
	/**
	 * Add the persons to this floor during the building initialization.
	 * @param personList
	 */
	public void addPerson (Person person) {
		this.personList.add(person);
		callbox.pressButton(getDirection(person));
	}
	
	/**
	 * Remove the person from this floor if he/she gets in the elevator
	 * @param personList
	 */
	public void removePerson (ArrayList<Person> removePersonList) {
		for(int i = 0; i < removePersonList.size(); i++) {
			for(int j = 0; j < personList.size(); j++) {
				if (removePersonList.get(i).getPersonId() == removePersonList.get(j).getPersonId())
					personList.remove(j);
			}
		}
	}
	
	/**
	 * Send a request to Controller with the direction.
	 * Means someone pressed a up/down button in this floor, then controller should send an elevator to this floor.
	 * @param direction
	 */
	public void pressButton (DIRECTION direction) {

		callbox.pressButton(direction);
		
	}
	
	/**
	 * Get the list of persons who are waiting for the elevator in this floor
	 * @return The list waiting person 
	 */
	public ArrayList<Person> getWaitingList() {
		ArrayList<Person> waitList = new ArrayList<Person>();
		for(int i = 0; i < personList.size(); i++) {
			if (personList.get(i).getStatus() == PERSON_STATUS.WAITING)
				waitList.add(personList.get(i));
		}
		return waitList;
	}
	
	private DIRECTION getDirection(Person person) {
		if (person.getToFloor() > person.getFromFloor())
			return DIRECTION.UP;
		else if (person.getToFloor() < person.getFromFloor())
			return DIRECTION.DOWN;
		else
			return DIRECTION.NONE;
	}
}
