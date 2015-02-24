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
	private ArrayList<Request> pendingList = new ArrayList<Request>(); 
	private boolean halt = false;
	private long threadInterval = 1000; //100
	String status_string = "";
	private static double  maxWaitingtimeLimit = -1;
	
	/**
	 * Get the singleton instance of ElevatorController.
	 * 
	 * @return The singleton instance of ElevatorController.
	 */
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
	
	/**
	 * Search the initial request in the pending list, which has the same direction as the first request, and is closest to the start floor of that direction.
	 * So that the elevator will respond all pending requests in the same direction.	
	 * 	 UP		: Closest to 1 floor.
	 *	 DOWN	: Closest to max floor. 
	 *
	 * @param ele - The specific elevator.
	 * @return The initial request for the elevator.
	 */
	public Request getInitialRequestForElevator(ElevatorImpl ele) {
		Request initialRequest = null;
		synchronized(this.pendingList) {
			Request firstRequest = null;
			 //If last floor request is UP, now elevator is idle, then prior for DOWN request in pendingList this time. 
			// To insure person's waiting time is smaller than floor*(opTime+travelTime)*2. 
			if (this.pendingList != null && this.pendingList.size() > 0) {		
				if (ele.lastFloorRequestDirection() != DIRECTION.NONE) {
					for (Request r : this.pendingList) {
						if (
							(ele.lastFloorRequestDirection()==DIRECTION.UP && r.direction ==DIRECTION.DOWN)
						||	(ele.lastFloorRequestDirection()==DIRECTION.DOWN && r.direction ==DIRECTION.UP)
							) {
							firstRequest = r;
							break;
						}
					}
				}
				
				if (firstRequest==null)
					firstRequest = this.pendingList.get(0);				
			}

			if (firstRequest != null) 
			{
				int closestDistance = Integer.MAX_VALUE;
				int maxFloor = ele.getMaxFloor();
				int selectedIndex = 0;
				for (int i = 0; i < this.pendingList.size(); i++) 
				{
					Request r = this.pendingList.get(i);
					int distance;
					if (r.direction == firstRequest.direction) {
						if (firstRequest.direction == DIRECTION.UP) 
							distance = Math.abs(r.floor - 1);				
						else 
							distance = Math.abs(maxFloor - r.floor);	
						
						if (distance < closestDistance) {
							closestDistance = distance;
							selectedIndex = i;
						}
					}					
				}
				
				initialRequest = this.pendingList.get(selectedIndex);
			}
		}		
		return initialRequest;
	}	
	
	/**
	 * Distribute pending requests to idle elevator. This function is invoked by the idle elevator itself.
	 * 
	 * @param elevator - The elevator to fetch pending request.
	 * @return true -  The elevator fetched at least one pending request.
	 * 		   false - The elevator fetched no pending request.
	 */
	public boolean handlePendingListForIdleElevator(ElevatorImpl elevator) throws InvalidParameterException {
		if (!elevator.isIdle()) {
			throw new InvalidParameterException("The elevator is not idle!");
		}				
		preprocess_pending_list();		
		boolean obtainedPendingRequest = false;
		synchronized(pendingList) {			
			Request initialRequest = getInitialRequestForElevator(elevator);
			if (initialRequest != null) {
				elevator.addRequest(initialRequest);
				pendingList.remove(initialRequest); 
			}
			
			for (int i= 0; i < pendingList.size(); i++) {
				Request request = pendingList.get(i);												
				if (elevator.isAvailableForRequest(request, true)) {
					elevator.addRequest(request);
					pendingList.remove(i); 
					i--;
					printPendingList();
				}
			}			
		}
		return obtainedPendingRequest;
	}
	
	/**
	 * Choose the best elevator to respond the request with shortest waiting time.
	 * 
	 * @param request 	- The new request needs to be responded. 
	 * @return true 	- Found best elevator to respond the request., and add the request to the corresponding elevator.
	 * 		   false	- No elevator is available.
	 */
	public boolean chooseElevatorForRequest(Request request) {
		boolean success = false;		
		ArrayList<Elevator> elevatorList = Building.getBuilding().getElevatorList();
		if (elevatorList==null)
			return false;		
		ElevatorImpl victorForRequest = null;		
		ArrayList<ElevatorImpl> availableElevators = new ArrayList<ElevatorImpl>();		
		// Get available elevators.
		for (int i = 0; i < elevatorList.size(); i++) 
		{
			ElevatorImpl ele = (ElevatorImpl)elevatorList.get(i);
			if (ele.isAvailableForRequest(request)) {
				availableElevators.add(ele);
			}
		}
		// Get the best elevator to respond the request with shortest waiting time.
		long shortestWaitingTime = Long.MAX_VALUE;
		for (int i = 0; i < availableElevators.size(); i++) {
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
	
	/**
	 * Pre-process the pending list before dispatching the pending requests to the idle elevators. 
	 */
	public void preprocess_pending_list() {
//		boolean pendingListIsEmpty = false;
//		synchronized(this.pendingList) {
//			if (this.pendingList.size() ==0) {
//				pendingListIsEmpty = true;
//			}
//		}
//		
//		if (pendingListIsEmpty) {
//				boolean areAllElevatorsIdle = true;
//				ArrayList<Elevator> elevatorList = Building.getBuilding().getElevatorList();
//				synchronized(elevatorList) {
//					for (Elevator ele : elevatorList) {
//						if (!((ElevatorImpl)ele).isIdle()){
//							areAllElevatorsIdle =false;
//							break;
//						}				
//					}				
//				}
//		
//				if (areAllElevatorsIdle) {					
//					ArrayList<Person> personList = Building.getBuilding().getPersonList();
//					boolean hasNewPendingRequest=false;
//					synchronized(personList) { 
//						for (Person person : personList) {
//							if (person.getStatus() == PERSON_STATUS.WAITING) {
//								hasNewPendingRequest = true;
//								this.addPendingRequest(Request.createWithPerson(person));
//							}
//						}
//					}		
//					
//					if (hasNewPendingRequest) {
//						synchronized(elevatorList) {
//							for (Elevator ele : elevatorList) {
//								((ElevatorImpl)ele).notifyRequestList(false);	
//							}				
//						}
//					}
//				}
//			}			
	}
		
	/**
	 * The function interface for the out caller to add floor request.
	 * 
	 * @param floor - The destination floor.
	 * @param direction - The direction of the request.
	 */
	public void addFloorRequest (int floor, DIRECTION direction) {
		Request request = new Request(REQUEST_TYPE.FLOOR, floor, direction);				
		if (!chooseElevatorForRequest(request)) {
			// No elevator is available.
			addPendingRequest(request);
		}		
	}
	
	@Override
	/**
	 * Check the status of all persons in the building.
	 */
	public void run() {
		while(!this.halt)
		{			
			try {							
				if (maxWaitingtimeLimit==-1) 
				{
					ElevatorImpl ele = (ElevatorImpl)Building.getBuilding().getElevatorList().get(0);
						if (ele != null) {
							maxWaitingtimeLimit = Building.getBuilding().getFloorNumbers() * (	ele.timePerFloor() + ele.timePerDoorOp() ) * 2;
						}
				}
			
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
					double maxWaitingtime=0; 						
					
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
							if (maxWaitingtime < person.getWaitTime())
								maxWaitingtime = person.getWaitTime();
							totalWaitingtime += person.getWaitTime();
							arrivedCount++;
							break;							
						}
					}
					
					if (arrivedCount > 0) {
						AvgWaitingtime = totalWaitingtime / arrivedCount;
					}
					
					String status_string = "ElevatorController -> PersonList -"
							+" Total:"+total				
							+" NONE:"+noneCount
							+" Waiting:"+waitingCount
							+" Riding:"+riddingCount
							+" Arrived:"+arrivedCount
							+" AvgWaitingtime:"+AvgWaitingtime	
//							+" maxWaitingtime:"+maxWaitingtime	
//							+"("
//							+ (maxWaitingtime <= maxWaitingtimeLimit ? "<=" : ">")
//							+ maxWaitingtimeLimit
//							+")"
							;				
					
					if (status_string.compareTo(this.status_string) !=0 ) 
					{
						Toolset.println("info", status_string);
						this.status_string = status_string;
					}
					
					// Elevator thread will be blocked if invoke wait(), so check time-out status here for elevators.
					ArrayList<Elevator> elevatorList = Building.getBuilding().getElevatorList();
					//synchronized(elevatorList) 
					{
						for (Elevator ele : elevatorList) {
							if ( ((ElevatorImpl)ele).isTimeOut()) 
							{
								((ElevatorImpl)ele).notifyForTimeOut();
							}
						}
					}					
				}
				
				Thread.sleep(threadInterval);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	/**
	 * Halt the thread.
	 */
	public void halt() {
		this.halt = true;
	}	
	
	/**
	 * Add one request to the pending list.
	 * 
	 * @param request - The new pending request.
	 */
	public void addPendingRequest(Request request) {
		synchronized(pendingList) {
			boolean alreadyExists = false;
			for (Request r : pendingList) {
				if (r.isEqualTo(request)) {
					alreadyExists = true;
					break;
				}					
			}			
			if (!alreadyExists) {
				pendingList.add(request);
				printPendingList();
			}
		}
	}
	
	/**
	 * Print the amount of the pending list.
	 */
	public void printPendingList() {
		Toolset.println("info", "ElevatorController -> PendingList amount: "+pendingList.size());
	}
	
	/**
	 * Get the algorithm strategy of the elevator controller.
	 * 
	 * @return The algorithm strategy.
	 */
	public STRATEGY_TYPE strategy() {
		return strategy;
	}
	
	/**
	 * Set the algorithm strategy of the elevator controller.
	 * 
	 * @param strategy - The algorithm strategy. ( LEAST_WAITING_TIME, LEAST_ELEVATOR_MOVEMENT)	
	 */	
	public void setStrategy(STRATEGY_TYPE strategy) {
		this.strategy = strategy;
	}	
}
