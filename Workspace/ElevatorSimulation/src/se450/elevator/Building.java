package se450.elevator;

import java.util.ArrayList;

import se450.elevator.common.ConfigDTO;
import se450.elevator.common.ConfigHelper;
import se450.elevator.common.Toolset;

/**
 * The building contains all of the elevators, persons, etc.
 * @author Rong Zhuang
 */
public class Building {
	
	private static Building instance = new Building();
	
	private ConfigDTO configDTO = new ConfigDTO();
	
	private ArrayList<Person> personList = new ArrayList<Person>();
	private ArrayList<Floor> floorList = new ArrayList<Floor>();
	private Controller controller = new Controller(); 
	
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
	 * @return
	 */
    public int getFloorNumbers() {
		return configDTO.getFloorNumbers();
	}    

    /**
	 * Get elevator numbers of the building
	 * @return
	 */
    public int getElevatorNumbers() {
		return configDTO.getElevatorNumbers();
	}    
 
    /**
	 * Get 'N' numbers for creating persons per minute
	 * @return
	 */
	public int getRandomPersonNumbers() {
		
		return configDTO.getRandomPersonNumbers();
	}
	
	/**
	 * Get the duration time for simulation
	 * @return
	 */
	public long getSimulationDuration() {
		
		return configDTO.getSimulationDuration();
	}
	
	/**
	 * Get the floor list of the building
	 * @return
	 */
	public ArrayList<Floor> getFloorsList()
	{
		return floorList;
	}
	
    /**
	 * Get all of the elevator list, including their attributes
	 * @return
	 */
    public ArrayList<Elevator> getElevatorList() {
		return configDTO.getElevatorList();
	}
    
    /**
	 * Get the person list which is generated by PersonGenerator
	 * 
	 */
    public ArrayList<Person> getPersonList() {
		//return configDTO.getPersonList();
    	return personList;
	}
    
    /**
	 * Get all of the dummy requests inside the elevator
	 * 
	 */
    public ArrayList<PanelRequest> getPanelRequestList() {
		return configDTO.getPanelRequestList();
	}
    
    /**
     * Get the elevator controller
     * @return
     */
    public Controller getElevatorController() {
    	return controller;
    }
    
    
    /**
	 * Set the person list which is generated by PersonGenerator
	 * @param personList
	 */
	public void setPersonList(ArrayList<Person> personList) {
		
		this.personList = personList;
	}
	
    /**
	 * Initialize the building, get all necessary configurations
	 * 
	 */
    public void initilize() {
    	Toolset.println("info", "Create Building...");
    	configDTO = ConfigHelper.getConfigDTO();
    	//Creating floors
    	for(int i = 0; i < configDTO.getFloorNumbers(); i++) {
			floorList.add(FloorFactory.createFloor());
		}
    	Toolset.println("info", "Building created - "+ configDTO.getFloorNumbers()+" floors, "+configDTO.getElevatorNumbers()+" elevators");
    }
}
