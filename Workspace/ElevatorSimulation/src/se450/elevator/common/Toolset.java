package se450.elevator.common;

import java.util.concurrent.TimeUnit;

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
		return String.format("%02d:%02d:%02d.%3d", 
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



}
