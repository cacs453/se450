/**
 * @ Cheng Zhang
 * @ 
 */

public class PersonFactory {
	private final static int DEFAULT_PERSON_TYPE = 0;
	
	public PersonFactory() {
	}
	
	public Person CreatePerson(int id, int fromFloor, int toFloor, int personType) {
		Person person = new PersonImpl(id, fromFloor, toFloor);
		return person;
	}
	
	public Person CreatePerson(int id, int fromFloor, int toFloor) {
		return CreatePerson(id, fromFloor, toFloor, DEFAULT_PERSON_TYPE);
	}
}
