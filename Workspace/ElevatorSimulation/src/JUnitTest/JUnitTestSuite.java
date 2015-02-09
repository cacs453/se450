package JUnitTest;

import junit.framework.TestSuite; 
import junit.framework.Test; 
import junit.textui.TestRunner; 
import se450.elevator.ElevatorFactory;

public class JUnitTestSuite extends TestSuite {
	public static Test suite() { 
        TestSuite suite = new TestSuite("TestSuite Test"); 
                        
        suite.addTestSuite(BuildingTest.class); 
        suite.addTestSuite(ControllerTest.class); 
        suite.addTestSuite(ElevatorButtonPanelTest.class); 
        suite.addTestSuite(ElevatorFactoryTest.class); 
        suite.addTestSuite(ElevatorImplTest.class); 
        suite.addTestSuite(FloorCallBoxTest.class); 
        suite.addTestSuite(FloorTest.class); 
        suite.addTestSuite(PersonFactoryTest.class); 
        return suite; 
    } 
	
    public static void main(String args[]){ 
        TestRunner.run(suite()); 
    } 
}
