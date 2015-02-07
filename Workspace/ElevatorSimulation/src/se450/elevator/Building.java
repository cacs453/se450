package se450.elevator;

import java.util.ArrayList;

import se450.elevator.common.ConfigHelper;

/**
 * 
 * @author Johnny
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
	    
    public static Building getBuilding() {
        return instance;
    } 
    
    public int getFloorNumbers() {
		return floorNumbers;
	}    
    
    public int getElevatorNumbers() {
		return elevatorNumbers;
	}    
 
    public ArrayList<Elevator> getElevatorList() {
		return elevatorList;
	}
    
    public ArrayList<Person> getPersonList() {
		return personList;
	}
    
    public ArrayList<PanelRequest> getPanelRequestList() {
		return panelRequestList;
	}
    
    public void initilize() {
    	floorNumbers = ConfigHelper.getFloorNumbers();
    	elevatorNumbers = ConfigHelper.getElevatorNumbers();
    	elevatorList = ConfigHelper.getElevatorList();
    	personList = ConfigHelper.getPersonList();
    	panelRequestList = ConfigHelper.getPanelRequestList();
    }
}
