package se450.elevator.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import se450.elevator.Person;

/**
 * Toolset shared for all project to get current delta time and do formated print.
 */
public class Toolset {

	private volatile static Toolset instance;
	private static long StartTimeMillis;

	/**
	 * Boolean flag for current running mode.
	 * true - Running on debug mode.
	 * false - Running on normal mode. Common.print("debug", String message) won't print anything at this mode.
	 */
	public static boolean DEBUG = false;

	private Toolset() {
		StartTimeMillis = System.currentTimeMillis();
	}

	/**
	 * Toolset is a Singleton class. This method will create the only Toolset instance.
	 * It is a multi-thread safe implementation.
	 * Make sure this method be invoked during program start so it knows the program start time.
	 */
	public static void init() {
		if (instance == null)
		{
			synchronized (Toolset.class)
			{
				if (instance == null) // Double-Check!
					instance = new Toolset();
			}
		}
	}

	/**
	 * Returns Delta time between program start and now in long format
	 */
	public static long getDeltaTimeLong() {
		return System.currentTimeMillis()-StartTimeMillis;
	}
	
	/**
	 * Returns Delta time between program start and now in String format "hh:MM:ss.mmm"
	 */
	public static String getDeltaTimeString() {
		long millis = getDeltaTimeLong();
		return String.format("%02d:%02d:%02d.%03d", 
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) - 
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
				millis % 1000);
	}
	
	/**
	 * Print a message with DeltaTimeMillis as pre-fix.
	 * 
	 * @param mode - Print mode in "debug", "error" or "info". If you input rather than the 3 modes, "info" will be used.
	 * @param message - The message to be printed
	 * @see #println(String, String)
	 */
	public static void print(String mode, String message) {
		String deltaTime = getDeltaTimeString();
		
		if (mode.equalsIgnoreCase("error"))
			System.err.print(deltaTime + " " + message);
		else if (mode.equalsIgnoreCase("debug")) {
			if (DEBUG)
				System.out.print(deltaTime + " " + message);
		}
		else
			System.out.print(deltaTime + " " + message);
	}
	
	/**
	 * Print a message with DeltaTimeMillis as pre-fix and followed by a new line.
	 * 
	 * @param mode - Print mode in "debug", "error" or "info". If you input rather than the 3 modes, "info" will be used.
	 * @param message - The message to be printed
	 * @see #print(String, String)
	 */
	public static void println(String mode, String message) {
		String deltaTime = getDeltaTimeString();
		
		if (mode.equalsIgnoreCase("error"))
			System.err.println(deltaTime + " " + message);
		else if (mode.equalsIgnoreCase("debug")) {
			if (DEBUG)
				System.out.println(deltaTime + " " + message);
		}
		else
			System.out.println(deltaTime + " " + message);
	}
/**
 * Print the statistic report
 * 
 * @param floorNumbers Floor numbers in int.
 * @param elevatorNumbers Elevator numbers in int.
 * @param personList ArrayList of Person.
 */
public static void printReport(int floorNumbers, int elevatorNumbers, ArrayList<Person> personList) {
		
		System.out.println("\nSimulation statistics:");
		// a) Average/Min/Max wait time by floor
		int[][] amm_floor = new int[floorNumbers][4];
		boolean[] flag_floor = new boolean[floorNumbers];
		for (int i=0; i<floorNumbers; i++) {
			for (int j=0; j<4; j++)
				amm_floor[i][j]=0;
			flag_floor[i]=false;
		}
		for (int i=0; i<personList.size(); i++) {
			Person ps = personList.get(i);
			int floor = ps.getFromFloor()-1;
			if (flag_floor[floor]) { //Have other records
				amm_floor[floor][0]+=(int)ps.getWaitTime();
				amm_floor[floor][1]+=1;
				if (ps.getWaitTime()<amm_floor[floor][2])
					amm_floor[floor][2]=(int)ps.getWaitTime();
				if (ps.getWaitTime()>amm_floor[floor][3])
					amm_floor[floor][3]=(int)ps.getWaitTime();
			}
			else { //First record
				flag_floor[floor]=true;
				amm_floor[floor][0]=(int)ps.getWaitTime();
				amm_floor[floor][1]=1;
				amm_floor[floor][2]=(int)ps.getWaitTime();
				amm_floor[floor][3]=(int)ps.getWaitTime();
			}
		}
		System.out.println("a) Average/Min/Max wait time by floor (in seconds)");
		System.out.println("Floor\tAverage\tMinimum\tMaximum");
		for (int i=0; i<floorNumbers; i++) {
			String msg = "  " + (i+1) + "  \t";
			if(flag_floor[i]) {
				msg += (int)Math.rint((double)amm_floor[i][0]/(double)amm_floor[i][1]/1000)+"s\t";
				msg += (int)Math.rint((double)amm_floor[i][2]/1000)+"s\t";
				msg += (int)Math.rint((double)amm_floor[i][3]/1000)+"s\t";
			}
			else {
				msg += "/\t/\t/";
			}
			System.out.println(msg);
		}
		System.out.println();
		
		// b) c) d) Average/Min/Max Ride Time from Floor to Floor by Person
		HashMap<String, Timer> timeMap = new HashMap<String, Timer>();
		for (int i=0; i<personList.size(); i++) {
			Person ps = personList.get(i);
			String key = ps.getFromFloor()+"-"+ps.getToFloor();
			if (timeMap.containsKey(key)) {
				Timer timer = timeMap.get(key);
				timer.total_time+=ps.getRideTime();
				timer.persons+=1;
				if (ps.getRideTime()<timer.min_time)
					timer.min_time=(int)ps.getRideTime();
				if (ps.getRideTime()>timer.max_time)
					timer.max_time=(int)ps.getRideTime();
			}
			else {
				Timer timer = new Timer((int)ps.getRideTime());
				timeMap.put(key, timer);
			}
		}
		System.out.print("b) Average Ride Time from Floor to Floor by Person (in seconds)\nFloor\t");
		for (int i=1; i<=floorNumbers; i++)
			System.out.print(i+"\t");
		System.out.println();
		for (int i=1; i<=floorNumbers; i++) {
			System.out.print("  "+i+"  \t");
			for (int j=1; j<=floorNumbers; j++) {
				if (timeMap.containsKey(i+"-"+j)) {
					Timer timer = timeMap.get(i+"-"+j);
					System.out.print(timer.total_time/timer.persons/1000+"s\t");
				}
				else {
					System.out.print("/\t");
				}
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("c) Max Ride Time from Floor to Floor by Person (in seconds)\nFloor\t");
		for (int i=1; i<=floorNumbers; i++)
			System.out.print(i+"\t");
		System.out.println();
		for (int i=1; i<=floorNumbers; i++) {
			System.out.print("  "+i+"  \t");
			for (int j=1; j<=floorNumbers; j++) {
				if (timeMap.containsKey(i+"-"+j)) {
					Timer timer = timeMap.get(i+"-"+j);
					System.out.print(timer.max_time/1000+"s\t");
				}
				else {
					System.out.print("/\t");
				}
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.print("d) Min Ride Time from Floor to Floor by Person (in seconds)\nFloor\t");
		for (int i=1; i<=floorNumbers; i++)
			System.out.print(i+"\t");
		System.out.println();
		for (int i=1; i<=floorNumbers; i++) {
			System.out.print("  "+i+"  \t");
			for (int j=1; j<=floorNumbers; j++) {
				if (timeMap.containsKey(i+"-"+j)) {
					Timer timer = timeMap.get(i+"-"+j);
					System.out.print(timer.min_time/1000+"s\t");
				}
				else {
					System.out.print("/\t");
				}
			}
			System.out.println();
		}
		System.out.println();
		
		// e) Wait/Ride/Total Time by Person
		System.out.println("e) Wait/Ride/Total Time by Person (in seconds)");
		System.out.println("Person\tStart_Floor\tDest_Floor\tWait_Time\tRide_Time\tTotal_Time");
		for (int i=0; i<personList.size(); i++) {
			Person ps = personList.get(i);
			System.out.println("P"+(i+1)+"\t"+ps.getFromFloor()+"\t\t"+ps.getToFloor()+"\t\t"+ps.getWaitTime()/1000+"s\t\t"+ps.getRideTime()/1000+"s\t\t"+(ps.getWaitTime()+ps.getRideTime())/1000+"s");
		}
	}

}

class Timer {
	public int total_time;
	public int persons;
	public int min_time;
	public int max_time;
	public Timer(int rideTime) {
		total_time=rideTime;
		persons=1;
		min_time=rideTime;
		max_time=rideTime;
	}
}
