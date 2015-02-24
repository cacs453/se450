/**
 * 
 */
package JUnitTest;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se450.elevator.Controller;
import se450.elevator.Request;
import se450.elevator.common.REQUEST_TYPE;
import se450.elevator.common.DIRECTION;


/**
 * @author Cheng Zhang
 *
 */
public class ControllerTest extends TestCase {
	private Controller controller=null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		controller = Controller.getInstance();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAddPendingRequest() {	
		Request request = new Request(REQUEST_TYPE.FLOOR, 3, DIRECTION.UP);
		controller.addPendingRequest(request);
		assertEquals(true, request.isEqualTo(controller.getPendingList().get(0)) );
	}
	
}
