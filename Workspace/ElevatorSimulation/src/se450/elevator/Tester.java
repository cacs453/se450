package se450.elevator;

import se450.elevator.common.Toolset;

public class Tester {

	public static void main(String[] args) {
		try {
			Toolset.init();
			Toolset.DEBUG = true;
			Thread.sleep(500);
			Toolset.print("debug", "Debug msg1\n");
			Thread.sleep(50);
			Toolset.print("error", "Error msg1\n");
			Thread.sleep(50);
			Toolset.print("info", "Info msg1\n");
			
			Toolset.DEBUG = false;
			Thread.sleep(500);
			Toolset.println("debug", "Debug msg2");
			Thread.sleep(50);
			Toolset.println("error", "Error msg2");
			Thread.sleep(50);
			Toolset.println("info", "Info msg2");
			
			Thread.sleep(60000);
			System.out.println(Toolset.getDeltaTimeLong());
			System.out.println(Toolset.getDeltaTimeString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}
