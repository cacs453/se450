package se450.elevator;

import java.util.ArrayList;

import se450.elevator.common.ConfigHelper;

/**
 * The building contains all of the elevators, persons, etc.
 *
 */
public class Building {
	
	private static Building instance = new Building();
	
	private int floorNumbers = 0;
	private int elevatorNumbers = 0;
	private ArrayList<Elevator> elevatorList = new ArrayList<Elevator>();
	private ArrayList<Person> personList = new ArrayList<Person>();
	private ArrayList<PanelRequest> panelRequestList = new ArrayList<PanelRequest>();
	
	private Building() {
	}
	
	/**
	 * Get the singleton instance of the building
	 * 
	 */
    public static Building getBuilding() {
        return instance;
    } 
    
    /**
	 * Get floor numbers of the building
	 * 
	 */
    public int getFloorNumbers() {
		return floorNumbers;
	}    

    /**
	 * Get elevator numbers of the building
	 * 
	 */
    public int getElevatorNumbers() {
		return elevatorNumbers;
	}    
 
    /**
	 * Get all of the elevator list, including their attributes
	 * 
	 */
    public ArrayList<Elevator> getElevatorList() {
		return elevatorList;
	}
    
    /**
	 * Get all of the person list, including their attributes
	 * 
	 */
    public ArrayList<Person> getPersonList() {
		return personList;
	}
    
    /**
	 * Get all of the dummy requests inside the elevator
	 * 
	 */
    public ArrayList<PanelRequest> getPanelRequestList() {
		return panelRequestList;
	}
    
    /**
	 * Initialize the building, get all necessary configurations
	 * 
	 */
    public void initilize() {
    	floorNumbers = ConfigHelper.getFloorNumbers();
    	elevatorNumbers = ConfigHelper.getElevatorNumbers();
    	elevatorList = ConfigHelper.getElevatorList();
    	personList = ConfigHelper.getPersonList();
    	panelRequestList = ConfigHelper.getPanelRequestList();
    }
}
