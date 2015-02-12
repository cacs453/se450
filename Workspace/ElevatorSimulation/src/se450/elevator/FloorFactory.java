package se450.elevator;
/**
 * Floor factory for creating floors. 
 */
public class FloorFactory {
	
	private static int floorId = 0;
	
	public static FloorImpl createFloor() {
		floorId++;
		return new FloorImpl(floorId);
	}
}
