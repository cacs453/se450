package se450.elevator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import se450.elevator.common.Toolset;

/**
 * Generator person randomly with the configuration from xml
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
	
	public PersonGenerator(int floornumbers, int randompersonnumbers, long simulationduration) {
		this.floornumbers = floornumbers;
		this.randompersonnumbers = randompersonnumbers;
		this.simulationduration = simulationduration;		
	}
	
	@Override
    public void run() {
        running = true; // Set to false when you want to end processing
        
        startTime = System.currentTimeMillis();
        generationSpan = (double)60 / randompersonnumbers; // The duration of each time for generating 'N' persons with one minute
        
        Toolset.println("info", "PersonGenerator -> Start to generate person randomly");
        
        // Start up and await first request
        while (running) {
            try {
                synchronized (personList) { // Wait for something to happen (request added or removed)
                	if (personList.size() >= randompersonnumbers)
                		running = false; // Stop running after creating enough persons
                	else {
                		int fromFloor = 0;
                		int toFloor = 0;
                		
            			do {
                    		fromFloor = randomWithRange(1, floornumbers);
                    		toFloor = randomWithRange(1, floornumbers);
            			}while(fromFloor == toFloor);
                				
	                	PersonImpl person = new PersonImpl(personCount++, fromFloor, toFloor, (System.currentTimeMillis()-startTime));
	                	personList.add(person);
	                	Toolset.println("info", String.format("PersonGenerator -> New Person [%d] is generated in floor [%s] with destination floor [%s]", person.getPersonId(), person.getFromFloor(), person.getToFloor()));
	                	Thread.sleep((long)(randomWithRange(generationSpan-generationSpan/2, generationSpan+generationSpan/2)*1000));	                	
                	}
                }
            } catch (InterruptedException ex) {
            	Toolset.println("info", "PersonGenerator 'run' is interrupted! Going back to check for requests/wait");
                continue;
            }

            if ((System.currentTimeMillis()-startTime) > simulationduration)
            	break;            
        }
        Toolset.println("info", "PersonGenerator -> Finished");
    }
	
	private int randomWithRange(int min, int max)
	{
	   int range = Math.abs(max - min) + 1;     
	   return (int)(Math.random() * range) + (min <= max ? min : max);
	}
	
	private double randomWithRange(double min, double max)
	{
	   double range = Math.abs(max - min);     
	   return (Math.random() * range) + (min <= max ? min : max);
	}
	
	private String getTimeStamp() {

        return new Timestamp(System.currentTimeMillis()).toString();

    }
	
	public ArrayList<Person> getPersonList() {
		return personList;
	}
}
