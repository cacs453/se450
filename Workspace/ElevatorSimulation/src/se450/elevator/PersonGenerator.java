package se450.elevator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

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
		this.simulationduration = simulationduration;
	}

	@Override
	/**
	 * This thread runs to create person according the settings from configration file.
	 */
	public void run() {
		running = true; // Set to false when you want to end processing

		startTime = System.currentTimeMillis();
		generationSpan = (double) 60 / randompersonnumbers; // The duration of
															// each time for
															// generating 'N'
															// persons with one
															// minute

		Toolset.println("info",
				"PersonGenerator -> Start to generate person randomly");

		// Start up and await first request
		while (running) {
			try {
				synchronized (personList) { // Wait for something to happen
											// (request added or removed)
					if (personList.size() >= randompersonnumbers)
						running = false; // Stop running after creating enough
											// persons
					else {
						int fromFloor = 0;
						int toFloor = 0;

						do {
							fromFloor = randomWithRange(1, floornumbers);
							toFloor = randomWithRange(1, floornumbers);
						} while (fromFloor == toFloor);

						PersonImpl person = new PersonImpl(personCount++,
								fromFloor, toFloor,
								(System.currentTimeMillis() - startTime));	
						Toolset.println(
								"info",
								String.format(
										"PersonGenerator -> New Person [%d] is generated in floor [%s] with destination floor [%s]. (%s)",
										person.getPersonId(),
										person.getFromFloor(),
										person.getToFloor(),
										Request.createWithPerson(person).toInfoString()
										));
						
						personList.add(person);
						addPersonToFloor(person); // Add person to specified floor						
						Thread.sleep((long) (randomWithRange(generationSpan
								- generationSpan / 2, generationSpan
								+ generationSpan / 2) * 1000));
					}
				}
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
	private void addPersonToFloor(PersonImpl person) {
		for(int i = 0; i < floorList.size(); i++) {
			FloorImpl floor = (FloorImpl)floorList.get(i);
			if (floor.getFloorId() == person.getFromFloor()) {				
				floor.addPerson(person);
				floor.setCallBox(person); //invoke the floor request immediately
				break;
			}
		}
	}
}
