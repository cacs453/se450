package se450.elevator;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import se450.elevator.common.*;
import se450.elevator.Request;

/**
 * The Elevator Controller will take care all the floor requests and dispatch them to each elevator depending on their running status
 * 
 * @author Cheng Zhang
 *
 */
public class Controller extends Thread {
	protected STRATEGY_TYPE strategy = STRATEGY_TYPE.LEAST_WAITING_TIME;
	private static Controller instance = null;
	private ArrayList<Request> pendingList = new ArrayList<Request>(); // Pending list.	
	private boolean halt = false;
	private long threadInterval = 100;
	String status_string = "";
	
	public static Controller getInstance() {
		if (instance == null) {
			synchronized(Controller.class) {
				if (instance == null) {
					instance = new Controller();
				}
			}
		}
		return instance;
	}
	
	public STRATEGY_TYPE strategy() {
		return strategy;
	}
	
	public void setStrategy(STRATEGY_TYPE strategy) {
		this.strategy = strategy;
	}
	
	@Override
	public void run() {
		while(!this.halt) {						
			try {
				ArrayList<Person> personList = Building.getBuilding().getPersonList();		
				synchronized(personList) {
					// Person list.
					int total=0;
					int noneCount=0;
					int waitingCount=0;
					int riddingCount=0; 
					int arrivedCount=0;
					int totalWaitingtime=0;				
					int AvgWaitingtime=0; 						
					
					total = personList.size();					
					for (Person person : personList) {
						switch (person.getStatus()) {
						case NONE:
							noneCount++;
							break;
						case WAITING:
							waitingCount++;
							break;
						case RIDDING:
							riddingCount++;
							break;
						case ARRIVED:
							totalWaitingtime += person.getWaitTime();
							arrivedCount++;
							break;							
						}
					}
					
					if (arrivedCount > 0) {
						AvgWaitingtime = totalWaitingtime / arrivedCount;
					}
					
					String status_string = "ElevatorController -> PersonList - "
							+" Total:"+total				
							+" NONE:"+noneCount
							+" Waiting:"+waitingCount
							+" Riding:"+riddingCount
							+" Arrived:"+arrivedCount
							+" AvgWaitingtime:"+AvgWaitingtime						
							;				
					
					if (status_string.compareTo(this.status_string) !=0 ) 
					{
						Toolset.println("info", status_string);
						this.status_string = status_string;
					}
					Thread.sleep(threadInterval);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
			
	}
	public void halt() {
		this.halt = true;
	}
	
	/**
	 * Distribute pending request to idle elevator. This function is called by idle elevator itself.
	 * @param elevator
	 */
	public void handlePendingListForIdleElevator(ElevatorImpl elevator) throws InvalidParameterException {
		if (!elevator.isIdle()) {
			throw new InvalidParameterException("The elevator is not idle!");
		}		
		synchronized(pendingList) {			
			for (int i= 0; i < pendingList.size(); i++) {
				Request request = pendingList.get(i);												
				if (elevator.isAvailableForRequest(request)) {
					elevator.addRequest(request);
					pendingList.remove(i); 
					i--;
					printPendingList();
				}
			}			
		}
	}
	
	/**
	 * Choose the best elevator to respond the request with shortest waiting time.
	 * @param request
	 * @return true - Found best elevator to respond the request., and add the request to the corresponding elevator.
	 * 		   false - No elevator is available.
	 */
	public boolean chooseElevatorForRequest(Request request) {
		boolean success = false;
		
		ArrayList<Elevator> elevatorList = Building.getBuilding().getElevatorList();
		ElevatorImpl victorForRequest = null;
		
		ArrayList<ElevatorImpl> availableElevators = new ArrayList<ElevatorImpl>();
		
		// Get available elevators.
		for (int i = 0; i < elevatorList.size(); i++) {
			ElevatorImpl ele = (ElevatorImpl)elevatorList.get(i);
			if (ele.isAvailableForRequest(request)) {
				availableElevators.add(ele);
			}
		}
		
		// Get the best elevator to respond the request with shortest waiting time.
		long shortestWaitingTime = Long.MAX_VALUE;
		for (int i = 0; i < availableElevators.size(); i++) {
			if(i==0) {
//				Toolset.println("info", "ElevatorController -> CompareWaitingTime for RequestType:"+
//						request.type+" RequestFloor:"+request.floor+" Direction:"+request.direction+
//						"");
			}
			ElevatorImpl ele = availableElevators.get(i);
			long waitingTime = ele.calculateWaitingTimeForRequest(request);
			if (waitingTime < shortestWaitingTime) {
				shortestWaitingTime = waitingTime;
				victorForRequest = ele;
			}				
		}
		
		if (victorForRequest != null) {
			victorForRequest.addRequest(request);
			success = true;
		}

		return success;
	}
	
	public void printPendingList() {
		Toolset.println("info", "ElevatorController -> PendingList count: "+pendingList.size());
	}
	
	public void addFloorRequest (int floor, DIRECTION direction) {
		Request request = new Request(REQUEST_TYPE.FLOOR, floor, direction);
				
		if (!chooseElevatorForRequest(request)) {
			// No elevator is available.
			synchronized(pendingList) {	
				pendingList.add(request);
				printPendingList();
			}
		}		
	}
}
