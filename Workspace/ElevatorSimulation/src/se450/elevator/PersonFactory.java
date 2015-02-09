/**
 * @ Cheng Zhang
 * @ 
 */
package se450.elevator;

/**
 * Person factory.
 */
public class PersonFactory {
	private final static int DEFAULT_PERSON_TYPE = 0;
	private int personID = 0;
	
	public PersonFactory() {
	}	
	
	/**
	 * Create a person.
	 * @param fromFloor - The floor where the person was from.
	 * @param toFloor - The floor where the person will go to.
	 * @param triggerTime - The preset time when the person will trigger the elevator button.
	 * @return Person instance.
	 */
	public Person CreatePerson(int fromFloor, int toFloor, long triggerTime) {
		Person person = new PersonImpl(personID, fromFloor, toFloor, triggerTime);
		personID++;
		return person;		
	}
}
