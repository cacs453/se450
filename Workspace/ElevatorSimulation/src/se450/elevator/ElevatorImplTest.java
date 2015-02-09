package se450.elevator;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ElevatorImplTest extends TestCase {
	protected ElevatorImpl e1= ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
	
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
	public void testAddFloorRequest() {		
		e1.addFloorRequest(11, DIRECTION.UP);
		e1.addFloorRequest(14, DIRECTION.UP);
		e1.addFloorRequest(15, DIRECTION.UP);
		
		String requestList = e1.requestList_toString();
	}
	
	@Test
	public void testAddRiderRequest() {

	}
	
	@Test
	public void testRequestList_toString() {		
		e1.addFloorRequest(11, DIRECTION.UP);
		e1.addFloorRequest(14, DIRECTION.UP);
		e1.addFloorRequest(15, DIRECTION.UP);		
		String requestList = e1.requestList_toString();
	}
	
	@Test
	public void testDistance() {		
	}
	
	@Test
	public void testIsNearer() {		
	}

}
