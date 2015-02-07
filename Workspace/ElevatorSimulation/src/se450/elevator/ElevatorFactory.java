package se450.elevator;

public class ElevatorFactory {
	static int elevatorID=0;
	public static ElevatorImpl createElevator(int maxPassenger, int maxFloor, int timePerFloor, int timePerDoorOp, int defaultFloor, int timeOut) {
		elevatorID++;
		return new ElevatorImpl(elevatorID, maxPassenger, maxFloor, timePerFloor, timePerDoorOp, defaultFloor, timeOut);
	}
}
