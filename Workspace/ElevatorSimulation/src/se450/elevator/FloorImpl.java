package se450.elevator;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import se450.elevator.common.DIRECTION;
import se450.elevator.common.PERSON_STATUS;
import se450.elevator.common.Toolset;

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
	 * @param person
	 */
	public void addPerson (Person person) {
		synchronized (personList) {
			this.personList.add(person);
		}
	}
	
	/**
	 * Add the persons to this floor in bulk mode
	 * @param persons
	 */
	public void addPersonBulk (ArrayList<Person> persons) {
		synchronized (personList) {
			//System.out.println("before->"+personList.size());
			this.personList.addAll(persons);
			//System.out.println("after->"+personList.size());
		}
	}
	
	/**
	 * Remove the person from this floor if he/she gets in the elevator
	 * @param removePersonList
	 */
	public void removePerson (ArrayList<Person> removePersonList) {
		synchronized (personList) {
			for(int i = 0; i < removePersonList.size(); i++) {
				for(int j = 0; j < personList.size(); j++) {
					if (removePersonList.get(i).getPersonId() == removePersonList.get(j).getPersonId()) {
						int personId = removePersonList.get(i).getPersonId();
						personList.remove(j);
						Toolset.println("info",	String.format("Person %d has left Floor %s [Floor People: %s]", personId, this.getFloorId(), getPersonListInfo(this.personList)));
						break;
					}
				}
			}
		}
	}
	
	public void addTravelledPerson (ArrayList<Person> travelledPersonList) {
		synchronized (personList) {
			this.personList.addAll(travelledPersonList);
			for(Person p: travelledPersonList) {
				Toolset.println("info",	String.format("Person %d entered Floor %d [People: %s]", p.getPersonId(), this.getFloorId(), getPersonListInfo(travelledPersonList)));
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
		synchronized (personList) {
			ArrayList<Person> waitList = new ArrayList<Person>();
			for(int i = 0; i < personList.size(); i++) {
				if (personList.get(i).getStatus() == PERSON_STATUS.WAITING)
					waitList.add(personList.get(i));
			}
			
			return waitList;
		}
	}
	
	/**
	 * Set call box for the person
	 * @param person
	 */
	public void setCallBox(Person person) {
		person.startWaiting();
		DIRECTION direction = getDirection(person);
		callbox.pressButton(direction);
		String requestInfo;
		if (direction == DIRECTION.DOWN)
			requestInfo = "Person %d presses DOWN button on Floor %s";
		else
			requestInfo = "Person %d presses UP button on Floor %s";
		Toolset.println("info",	String.format(requestInfo, person.getPersonId(), this.getFloorId()));
	}	
	
	/**
	 * Set call box at once for multiple persons
	 * @param persons
	 */
	public void setCallBoxBulk(ArrayList<Person> persons) {
		boolean upPressed = false;
		boolean downPressed = false;
		
		for(int i=0; i<persons.size(); i++) {
			//callbox.pressButton(getDirection((PersonImpl)persons.get(i)));
			Person person = persons.get(i);
			person.startWaiting();
			DIRECTION direction = getDirection(person);
			if (direction == DIRECTION.UP) {
				if(!upPressed) {
					callbox.pressButton(DIRECTION.UP);
					upPressed = true;
				}
			}
			else {
				if(!downPressed) {
					callbox.pressButton(DIRECTION.DOWN);
					downPressed = true;
				}
			}
			
			String requestInfo;
			if (direction == DIRECTION.DOWN)
				requestInfo = "Person %d presses DOWN button on Floor %s";
			else
				requestInfo = "Person %d presses UP button on Floor %s";
			Toolset.println("info",	String.format(requestInfo, person.getPersonId(), this.getFloorId()));
		}
	}
	
	/**
	 * Get person's direction
	 * @param person
	 * @return
	 */
	private DIRECTION getDirection(Person person) {
		if (person.getToFloor() > person.getFromFloor())
			return DIRECTION.UP;
		else if (person.getToFloor() < person.getFromFloor())
			return DIRECTION.DOWN;
		else
			return DIRECTION.NONE;
	}
	
	/**
	 * Get the printing person list information
	 * @return
	 */
	private String getPersonListInfo(ArrayList<Person> persons) {
		StringBuilder sb = new StringBuilder();
		for(Person p: persons) {			
			sb.append(p.getPersonId());
			sb.append(",");			
		}
		return sb.toString();
	}
}
