package se450.elevator.common;

import java.util.ArrayList;

import se450.elevator.Elevator;
import se450.elevator.PanelRequest;
import se450.elevator.Person;

/**
 * DTO for the configurations from XML file
 * @author Rong Zhuang
 *
 */
public class ConfigDTO {
	
	private int floorNumbers = 0;
	private int elevatorNumbers = 0;
	private int randomPersonNumbers = 0;
	private long simulationDuration = 0;
	
	private ArrayList<Elevator> elevatorList = new ArrayList<Elevator>();
	private ArrayList<Person> personList = new ArrayList<Person>();
	private ArrayList<PanelRequest> panelRequestList = new ArrayList<PanelRequest>();
	
	public ConfigDTO() {
		
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
	 * Get 'N' numbers for creating persons per minute
	 * 
	 */
	public int getRandomPersonNumbers() {
		
		return randomPersonNumbers;
	}
	
	/**
	 * Get the duration time for simulation
	 * 
	 */
	public long getSimulationDuration() {
		
		return simulationDuration;
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
	 * Set floor numbers of the building
	 * @param floorNumbers
	 */
	public void setFloorNumbers(int floorNumbers) {
		this.floorNumbers = floorNumbers;
	}
	
	/**
	 * Set elevator numbers of the building
	 * @param elevatorNumbers
	 */
	public void setElevatorNumbers(int elevatorNumbers) {
		
		this.elevatorNumbers = elevatorNumbers;
	}
	
	/**
	 * Set 'N' numbers for creating persons per minute
	 * @param randomPersonNumbers
	 */
	public void setRandomPersonNumbers(int randomPersonNumbers) {
		
		this.randomPersonNumbers = randomPersonNumbers;
	}
	
	/**
	 * Set the duration time for simulation
	 * @param simulationDuration
	 */
	public void setSimulationDuration(long simulationDuration) {
		
		this.simulationDuration = simulationDuration;
	}
	
	/**
	 * Set all of the elevator list, including their attributes
	 * @param elevatorList
	 */
	public void setElevatorList(ArrayList<Elevator> elevatorList) {
		
		this.elevatorList = elevatorList;
	}
	
	/**
	 * Set all of the person list, including their attributes
	 * @param personList
	 */
	public void setPersonList(ArrayList<Person> personList) {
		
		this.personList = personList;
	}
	
	/**
	 * Set all of the dummy requests inside the elevator
	 * @param panelRequestList
	 */
	public void setPanelRequestList(ArrayList<PanelRequest> panelRequestList) {
		
		this.panelRequestList = panelRequestList;
	}
}
