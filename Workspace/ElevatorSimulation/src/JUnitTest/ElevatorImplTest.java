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
import se450.elevator.Elevator;
import se450.elevator.Request;
import se450.elevator.common.DIRECTION;
import se450.elevator.common.REQUEST_TYPE;

/**
 * JUNIT test.
 * 
 * @author Cheng Zhang
 *
 */
public class ElevatorImplTest extends TestCase {	
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
	public  void testAddFloorRequest() throws Exception {	
		ElevatorImpl e1= ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);		
		e1.start();
		e1.addFloorRequest(11, DIRECTION.UP);
		Thread.sleep(60);
		e1.addFloorRequest(14, DIRECTION.UP);
		e1.addFloorRequest(12, DIRECTION.UP);				
		String requestList = e1.requestList_toString();
		System.out.println(requestList);
		e1.halt();		
		assertEquals(
				"[Floor Requests: 11, 12, 14, ] [Rider Requests: ]", 
				requestList);
	}
	
	@Test
	public void testAddRiderRequest()  throws Exception{
		ElevatorImpl e1= ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);		
		e1.start();
		e1.addRiderRequest(11);
		Thread.sleep(60);		
		e1.addRiderRequest(14);
		e1.addRiderRequest(12);				
		String requestList = e1.requestList_toString();
		System.out.println(requestList);
		e1.halt();		
		assertEquals(
				"[Floor Requests: ] [Rider Requests: 11, 12, 14, ]", 
				requestList);
	}
	
	@Test
	public void testRequestList_toString()  throws Exception{		
		ElevatorImpl e1= ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
		e1.start();
		e1.addFloorRequest(11, DIRECTION.UP);
		Thread.sleep(600);
		e1.addFloorRequest(14, DIRECTION.UP);
		e1.addFloorRequest(12, DIRECTION.UP);				
		String requestList = e1.requestList_toString();
		System.out.println(requestList);
		e1.halt();		
		assertEquals(
				"[Floor Requests: 11, 12, 14, ] [Rider Requests: ]", 
				requestList);
	}
	
	@Test
	public void testDistance()  throws Exception{	
		ElevatorImpl e1= ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
		e1.start();		
		e1.addFloorRequest(11, DIRECTION.UP);
		Thread.sleep(60);		
		assertEquals(10, e1.distance(new Request(REQUEST_TYPE.FLOOR, 11, DIRECTION.UP)));
		assertEquals(14, e1.distance(new Request(REQUEST_TYPE.FLOOR, 15, DIRECTION.UP)));
		assertEquals(30, e1.distance(new Request(REQUEST_TYPE.FLOOR, 1, DIRECTION.DOWN)));
		e1.halt();	
	}
	
	@Test
	public void testIsNearer()  throws Exception{	
		ElevatorImpl e1= ElevatorFactory.createElevator(10, 16, 500, 500, 1, 15000);
		e1.start();		
		e1.addFloorRequest(11, DIRECTION.UP);
		Thread.sleep(600);				
		assertEquals(true, e1.isNearer(new Request(REQUEST_TYPE.FLOOR, 11, DIRECTION.UP), 
									   new Request(REQUEST_TYPE.FLOOR, 12, DIRECTION.UP)));		
		assertEquals(false, e1.isNearer(new Request(REQUEST_TYPE.FLOOR, 12, DIRECTION.UP), 
				   new Request(REQUEST_TYPE.FLOOR, 11, DIRECTION.UP)));

		assertEquals(true, e1.isNearer(new Request(REQUEST_TYPE.FLOOR, 12, DIRECTION.UP), 
				   new Request(REQUEST_TYPE.FLOOR, 12, DIRECTION.DOWN)));

		assertEquals(true, e1.isNearer(new Request(REQUEST_TYPE.FLOOR, 15, DIRECTION.UP), 
				   new Request(REQUEST_TYPE.FLOOR, 12, DIRECTION.DOWN)));
		e1.halt();	
	}

}
