package JUnitTest;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se450.elevator.ElevatorFactory;
import se450.elevator.ElevatorImpl;

public class ElevatorFactoryTest extends TestCase {

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
	public void testCreateElevator() {
		ElevatorImpl e1 = ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
		assertNotNull(e1);
		assertEquals(1,e1.getElevatorID(),0); 
	}
}
