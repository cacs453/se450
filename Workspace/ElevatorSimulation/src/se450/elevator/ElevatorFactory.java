package se450.elevator;


/**
 * This is the Elevator Factory class. Use this class to create Elevator instances.
 * 
 * @author Shan Gao
 *
 */
public class ElevatorFactory {
	static int elevatorID=0;
	/**
	 * Invoke this method to create an Elevator instance.
	 * 
	 * @param maxPassenger - Max capacity of this elevator, int.
	 * @param maxFloor - Max floor of this elevator will go, int.
	 * @param timePerFloor - How long does this elevator spend per go UP/DOWN 1 floor. Millisecond, int. 
	 * @param timePerDoorOp - How long does this elevator spend per door open and then close. Millisecond, int. 
	 * @param defaultFloor - Default floor of this elevator, int.
	 * @param timeOut - If this elevator idles longer than this time, it will go to the {@link #defaultFloor}. Millisecond, int.
	 * @return - An instance of an newly created elevator.
	 */
	public static ElevatorImpl createElevator(int maxPassenger, int maxFloor, int timePerFloor, int timePerDoorOp, int defaultFloor, int timeOut) {
		elevatorID++;
		return new ElevatorImpl(elevatorID, maxPassenger, maxFloor, timePerFloor, timePerDoorOp, defaultFloor, timeOut);
	}
}
