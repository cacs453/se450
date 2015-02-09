package se450.elevator;

/**
 * Elevator Button Panel.
 * For Phase II, the class is not used. The logic is embeded in ElevatorImpl class
 * 
 * @author Cheng Zhang
 *
 */
public class ElevatorButtonPanel {	
	private static int status; 
	private static Elevator parentElevator;
	
	public ElevatorButtonPanel(Elevator elevator) {
		// TODO Auto-generated constructor stub
	}	
		
	public void pressFloor(Person person, int floor) {
		
	}
	
	// reset the specific floor button to normal.
	public void resetFloor(int floor) {
		
	}
	
	public boolean isFloorPressed(int floor) {
		return false;
	}	
}
