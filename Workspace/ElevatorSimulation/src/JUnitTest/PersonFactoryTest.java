package JUnitTest;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se450.elevator.PersonFactory;
import se450.elevator.Person;
import se450.elevator.ElevatorFactory;
import se450.elevator.ElevatorImpl;

public class PersonFactoryTest extends TestCase {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPersonFactory() {
		Person person = PersonFactory.CreatePerson(1, 12, 5000);
		assertNotNull(person);
	}

}
