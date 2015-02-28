package se450.elevator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import se450.elevator.common.Toolset;

/**
 * Generator person randomly with the configuration from xml
 * 
 * @author Rong Zhuang
 *
 */
public class PersonGenerator implements Runnable {

	private int floornumbers = 0;
	private int randompersonnumbers = 0;
	private long simulationduration = 0;
	private boolean running = false;
	private long startTime;
	private int personCount = 1;
	private double generationSpan = 0;
	private final ArrayList<Person> personList = new ArrayList<Person>();
	private ArrayList<Floor> floorList = new ArrayList<Floor>();
	private int bulkBlock = 0;
	private int bulkCount = 0;
	private HashMap<Integer, ArrayList<Person>> map = new HashMap<Integer, ArrayList<Person>>();
	private String personInfo;
	
	/**
	 * Constructor of PersonGenerator
	 * @param floornumbers, floor numbers of the building
	 * @param randompersonnumbers, the number how many person need to be created within one minute
	 * @param simulationduration, the duration for the simulation, this generator will stop working once time exceeds
	 */
	public PersonGenerator(int floornumbers, int randompersonnumbers,
			long simulationduration) {
		this.floornumbers = floornumbers;
		this.randompersonnumbers = randompersonnumbers;
		this.simulationduration = simulationduration * 1000;
		if (randompersonnumbers<100)
			this.bulkBlock = 0;
		else
			this.bulkBlock = randompersonnumbers/27;
	}

	@Override
	/**
	 * This thread runs to create person according the settings from configration file.
	 */
	public void run() {
		running = true; // Set to false when you want to end processing

		startTime = System.currentTimeMillis();
		
		int offset = 0; //based on 4 elevators
		if (randompersonnumbers < 50)
			offset = 3; 
		else if (randompersonnumbers >= 50&&randompersonnumbers < 200)
			offset = 15; 
		else if (randompersonnumbers >= 200&&randompersonnumbers < 500)
			offset = 25; 
		else if (randompersonnumbers >= 500&&randompersonnumbers < 1000)
			offset = 32; 
		else if (randompersonnumbers >= 1000&&randompersonnumbers < 5000)
			offset = 44; 
		else if (randompersonnumbers >= 5000&&randompersonnumbers < 10000)
			offset = 50; 
		else
			offset = 55; 
		
		int elevators = Building.getBuilding().getElevatorNumbers();
		if (elevators<4)
			offset = offset- (int)(offset * Math.pow(0.83, (double)(4-elevators)));		
		
		if (offset>=60)
			offset = 0;
		
		generationSpan = (double) (60-offset) / randompersonnumbers; 	// The duration of
																		// each time for
																		// generating 'N'
																		// persons with one
																		// minute
		
		if (randompersonnumbers>4000&&elevators>=4)
			generationSpan = 0;

		Toolset.println("info",
				"PersonGenerator -> Start to generate person randomly");

		// Start up and await first request
		while (running) {
			try {
				Person person=null;
				synchronized (personList) { // Wait for something to happen
											// (request added or removed)
					if (personList.size() >= randompersonnumbers) {
						sendPersonToFloor(); // Force to call
						running = false; // Stop running after creating enough
					}
					else {
						int fromFloor = 0;
						int toFloor = 0;

						do {
							fromFloor = randomWithRange(1, floornumbers);
							toFloor = randomWithRange(1, floornumbers);
						} while (fromFloor == toFloor);

						person = PersonFactory.CreatePerson(fromFloor, toFloor, (System.currentTimeMillis() - startTime));
						if (fromFloor<toFloor)
							personInfo = "Person %d created on Floor %s, wants to go UP to Floor %s";
						else
							personInfo = "Person %d created on Floor %s, wants to go DOWN to Floor %s";
						
						Toolset.println(
								"info",
								String.format(
										personInfo,
										person.getPersonId(),
										person.getFromFloor(),
										person.getToFloor()
										));
						
						personList.add(person);												
					}					
				}
				if (person!=null)
					addPersonToFloor(person); // Add person to specified floor
				Thread.sleep((long) (randomWithRange(generationSpan
						- generationSpan / 2, generationSpan
						+ generationSpan / 2) * 1000));
			} catch (InterruptedException ex) {
				Toolset.println("info",
						"PersonGenerator 'run' is interrupted! Going back to check for requests/wait");
				continue;
			}

			if ((System.currentTimeMillis() - startTime) > simulationduration)
				break;
		}
		/*try {
			Thread.sleep(1000*120);
			Toolset.println("info", "PersonGenerator -> Totally created("+ personList.size() + ") persons");
		}
		catch(InterruptedException ex)
		{
			Toolset.println("info", ex.getStackTrace().toString());
		}*/
		Controller.getInstance().onPGFinished();
		Toolset.println("info", "PersonGenerator -> Finished");
	}

	/**
	 * Get random number(int) within the specified range
	 * @param min, the minimum value(included)
	 * @param max, the maximum value(included)
	 * @return the random number(int)
	 */
	private int randomWithRange(int min, int max) {
		int range = Math.abs(max - min) + 1;
		return (int) (Math.random() * range) + (min <= max ? min : max);
	}

	/**
	 * Get random number(double) within the specified range
	 * @param min, the minimum value(included)
	 * @param max, the maximum value(included)
	 * @return the random number(double)
	 */
	private double randomWithRange(double min, double max) {
		double range = Math.abs(max - min);
		return (Math.random() * range) + (min <= max ? min : max);
	}

	/**
	 * get timestamp for the current time
	 * @return the string format of the current time
	 */
	private String getTimeStamp() {

		return new Timestamp(System.currentTimeMillis()).toString();

	}
 
	/**
	 * Get the personlist created by the Generator.
	 * @return Generated personList 
	 */
	
	public ArrayList<Person> getPersonList() {
		return personList;
	}

	/**
	 * Set the floor list so that the floor request can be created once person is generated
	 * @param floorList
	 */
	public void setFloorList(ArrayList<Floor> floorList) {
		this.floorList = floorList;
	}

	
	/**
	 * Add person to floor and create the floor request immediately
	 * @param person, the person who is to be added to the specified floor
	 */
	/*private void addPersonToFloor(PersonImpl person) {
		for(int i = 0; i < floorList.size(); i++) {
			FloorImpl floor = (FloorImpl)floorList.get(i);
			if (floor.getFloorId() == person.getFromFloor()) {				
				floor.addPerson(person);
				floor.setCallBox(person); //invoke the floor request immediately
				break;
			}
		}
	}*/
	
	/**
	 * Cache the person list, and insert later by bulk. This is to avoid the lock.
	 * @param person instance
	 */
	private void addPersonToFloor(Person person) {
		//bulkPersonList.add(person);
		if (!map.containsKey(person.getFromFloor())) {
			ArrayList<Person> persons = new ArrayList<Person>();
			persons.add(person);
			map.put(person.getFromFloor(), persons);
		}
		else {
			ArrayList<Person> persons = map.get(person.getFromFloor());
			persons.add(person);
			map.remove(person.getFromFloor());
			map.put(person.getFromFloor(), persons);
		}
		
		bulkCount++;
		//Toolset.println("info", "PersonGenerator->bulkCount:" + bulkCount);
		if (bulkCount>=bulkBlock) {
			sendPersonToFloor();
			bulkCount = 0;
		}
	}
	
	/**
	 * Send person list to their floor in bulk mode
	 */
	private void sendPersonToFloor() {
		//Toolset.println("info", "PersonGenerator->sendPersonToFloor()->bulkBlock:" + bulkCount +"&bulkCount="+bulkCount);
		for(int i = 0; i < floorList.size(); i++) {
			FloorImpl floor = (FloorImpl)floorList.get(i);
			ArrayList<Person> persons = map.get(floor.getFloorId());
			if (persons!=null&&!persons.isEmpty()) {
				floor.addPersonBulk(persons);
				floor.setCallBoxBulk(persons); //invoke the floor request immediately
			}
		}
		map.clear();
	}
}
