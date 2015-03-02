package se450.elevator;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import se450.elevator.common.*;

/**
 * The implementation of Elevator interface. It also extends Thread class. It contains the most important logic for elevator movement.
 * 
 * @author Shan Gao
 */
public class ElevatorImpl extends Thread implements Elevator {

	private int elevatorID;
	private int maxPassenger;
	private int maxFloor;
	private int timePerFloor;
	private int timePerDoorOp;
	private int defaultFloor;
	private int timeOut;
	private long lastDoorCloseTime;
	// The requested , although this.currentFloor .
	private int lastPassedFloor = -1; // Indicates last floor being passed(Door closed), for the condition that the elevator is between two floors.
	private DIRECTION movingDirection;
	protected int currentFloor;
	private ArrayList<Person> currentRider;
	private ArrayList<Request> requestList;
	//private ElevatorButtonPanel buttonPanel;
	private boolean halt;
	private boolean isBlocking = false; // Thread is being blocked by wait(). 
	//private long checkInterval = 100; //Sleep interval between each time elevator check its status
	private long lastFloorPassingTime;
	private boolean hasPrinted = false;
	 //If last floor request is UP, now elevator is idle, then prior for DOWN request in pendingList this time. 
	// To insure person's waiting time is smaller than floor*(opTime+travelTime)*2. 
	private DIRECTION lastFloorRequestDirection = DIRECTION.NONE;

	/**
	 * Constructor of Elevator. Need to input default parameters of this Elevator instance;
	 * 
	 * @param elevatorID - The id of this elevator, int.
	 * @param maxPassenger - Max capacity of this elevator, int.
	 * @param maxFloor - Max floor of this elevator will go, int.
	 * @param timePerFloor - How long does this elevator spend per go UP/DOWN 1 floor. Millisecond, int. 
	 * @param timePerDoorOp - How long does this elevator spend per door open and then close. Millisecond, int. 
	 * @param defaultFloor - Default floor of this elevator, int.
	 * @param timeOut - If this elevator idles longer than this time, it will go to the {@link #defaultFloor}. Millisecond, int.
	 */
	public ElevatorImpl(int elevatorID, int maxPassenger, int maxFloor, int timePerFloor, int timePerDoorOp, int defaultFloor, int timeOut) 
			throws InvalidParameterException{

		if (elevatorID<=0 || maxPassenger<=0 || timePerFloor<=0 || timePerDoorOp<=0 || defaultFloor<=0 || timeOut<=0)
			throw new InvalidParameterException("The numbers must greater than zero.");

		this.elevatorID = elevatorID;
		this.maxPassenger = maxPassenger;
		this.maxFloor = maxFloor;
		this.timePerFloor = timePerFloor;
		this.timePerDoorOp = timePerDoorOp;
		this.defaultFloor = defaultFloor;
		this.timeOut = timeOut;
		//this.timeOut = Integer.MAX_VALUE; // For test mode.

		this.lastDoorCloseTime = Toolset.getDeltaTimeLong();
		//this.isMoving = false;
		this.movingDirection = DIRECTION.NONE;
		this.currentFloor = defaultFloor;
		this.currentRider = new ArrayList<Person>();
		this.requestList = new ArrayList<Request>();
		//this.buttonPanel = new ElevatorButtonPanel(this);
		this.halt = false;

		Toolset.println("debug", "Elevator "+elevatorID+" created.");
	}	
	
	@Override
	/**
	 * Elevator will continue work till Elevator.halt() function invoked.
	 * Each loop the elevator will check its request array then go to sleep for checkInterval.
	 */
	public void run() {
		while (!this.halt) {
			try {
				synchronized(this.requestList) {								
					if (this.movingDirection==DIRECTION.NONE) { //Idling
						if (this.requestList.size()==0) { //No task
							boolean shouldFetchPendingList = false; 
							if (Toolset.getDeltaTimeLong()-this.lastDoorCloseTime>=this.timeOut) 
							{ //Time out, go to default floor
								if (this.currentFloor!=this.defaultFloor)
									this.requestList.add(new Request(REQUEST_TYPE.TIMEOUT, this.defaultFloor, DIRECTION.NONE));
								else 
									shouldFetchPendingList = true;
							}
							else 
							{ //Continue idling								
								shouldFetchPendingList = true;
								Toolset.println("debug", "Elevator "+this.elevatorID+" is idling.");							
							}
							
							// Check pending list in Elevator Controller
							if (shouldFetchPendingList)
								Controller.getInstance().handlePendingListForIdleElevator(this);	
							
					         try {// Wait for something to happen (request added or removed). 
						        	 synchronized(this.requestList) //wait must operate the object with synchronized lock.
						        	 {
										if (this.requestList.size()==0) {// Maybe larger than 0 after invoking handlePendingListForIdleElevator.
											// Start waiting by multiple-thread concurrency method, it will respond in real time.
											// wait(): Causes the current thread to wait until another thread invokes the notify() method or the notifyAll() 
											this.isBlocking = true;											
											this.requestList.wait();
										}		
						        	 }
					            } catch (InterruptedException ex) {
					                System.out.println("Interrupted! Going back to check for requests/wait");
					            }
						}
						else { //Get something to do
							Request request = this.requestList.get(0);
							//Decide a new moving direction
							decide_new_moving_direction(request);
							//Record passing time
							this.lastFloorPassingTime = Toolset.getDeltaTimeLong();
							
							String msg = "Elevator "+this.elevatorID;
							if (request.type==REQUEST_TYPE.TIMEOUT)
								msg = msg + " times out - returning to default floor " + this.defaultFloor;
							else if (request.type==REQUEST_TYPE.FLOOR)
								msg = msg + " going to Floor "+request.floor+" for "+request.direction+" request "+ requestList_toString();
							else
								msg = msg + " going to Floor "+request.floor+" by rider request "+ requestList_toString();
							Toolset.println("info", msg);
						}
					}
					else { //Moving
						Request request = this.requestList.get(0);					
						
						if (this.currentFloor==request.floor) { //Arrived at the requested floor
							
							//Remove the request(s) from the list
							while (this.requestList.size()>0 && this.requestList.get(0).floor==this.currentFloor) {							
								if (this.requestList.get(0).type == REQUEST_TYPE.FLOOR ) {
									// Save the direction of last floor request.
									lastFloorRequestDirection = this.requestList.get(0).direction;
								}
								this.requestList.remove(0);
							}
							
							if (request.type==REQUEST_TYPE.TIMEOUT) { //Arrived default floor because time out
								Toolset.println("info", "Elevator "+this.elevatorID+" has arrived at Default Floor after Timeout");
							}
							else {
								//Open/close the door
								this.lastPassedFloor = this.currentFloor;
								
								String msg = "Elevator "+this.elevatorID+" has arrived at Floor "+this.currentFloor+" for ";
								if (request.type==REQUEST_TYPE.FLOOR)
									msg = msg + request.direction + " request";
								else
									msg = msg + "Rider Request";
								Toolset.println("info", msg);
								Toolset.println("info", "Elevator "+this.elevatorID+" Doors Open");
								
								if (this.requestList.size()==0) { //No other task
									this.movingDirection=DIRECTION.NONE;
								}
								else {
									//Decide a new moving direction
									decide_new_moving_direction(this.requestList.get(0));
								}						
								//Switch person status and add/remove corresponding requests.
								onArriveAtFloor(this.currentFloor);	
								
								Thread.sleep(this.timePerDoorOp);
								Toolset.println("info", "Elevator "+this.elevatorID+" Doors Close");
								this.lastDoorCloseTime = Toolset.getDeltaTimeLong();						
							}								
							
							if (this.requestList.size()==0) { //No other task
								this.movingDirection=DIRECTION.NONE;
							}
							else {
								//Decide a new moving direction
								decide_new_moving_direction(this.requestList.get(0));
							}
	
							//Record passing time
							this.lastFloorPassingTime = Toolset.getDeltaTimeLong();
						}
						else { //On the way
							if (!hasPrinted) { 			
								this.lastPassedFloor = this.currentFloor;
								
								String directionFlag = "";//(this.movingDirection==DIRECTION.UP) ? " ^"  : " v";
								String msg = "Elevator "+this.elevatorID+" moving"+directionFlag+" from Floor "+this.currentFloor+" to Floor ";
								if (this.movingDirection==DIRECTION.UP)
									msg = msg + (this.currentFloor+1);
								else if (this.movingDirection==DIRECTION.DOWN)
									msg = msg + (this.currentFloor-1);
								msg = msg + " " + requestList_toString();
								Toolset.println("info", msg);
								hasPrinted = true;
							}
							
							if (Toolset.getDeltaTimeLong()-this.lastFloorPassingTime>=this.timePerFloor) { //Arrived a new floor
								if (this.movingDirection==DIRECTION.UP)
									this.currentFloor++;
								else if (this.movingDirection==DIRECTION.DOWN)
									this.currentFloor--;
								this.lastFloorPassingTime = Toolset.getDeltaTimeLong();
								hasPrinted = false;
							}
						}
					}
	
	//				Thread.sleep(this.checkInterval);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
		}
		Toolset.println("debug", "Elevator "+this.elevatorID+" exists per request.");
	}	
	
	/**
	 * We need to calculate the elevator intending direction when call distance(), if there is request in list, but the direction has not been set yet.
	 * e.g. 
	 * 		currentFloor = 1,
	 * 	  
	 * 		floorRequest1.floor = 6, floorRequest1.direction=UP;
	 * 		floorRequest2.floor = 3, floorRequest1.direction=UP.
	 * 
	 * 		if interval between two requests is less than checkInterval, which is not necessarily guaranteed,  
	 * 		Sequence in the request list will to be 6, 3 (Correct sequence is 3, 6).
	 * 			 
	 * The reason is that when floorRequest2 being added, the direction of the elevator is still DIRECTION.NONE, 
	 * which leads to distance() function get the same value(Integer.MAX_VALUE) for floor 6 and floor3. 
	 * 
	 * This function is significant for elevator controller to make the right decision. 
	 * 	
	 * @param request - The request that decides the elevator direction.
	 * @return The elevator direction that calculated by the specific request.
	 * 
	 */
	protected DIRECTION caculate_direction_by_request(Request request) {
		DIRECTION direction = DIRECTION.NONE;		
		if (request.floor>this.currentFloor)
			direction=DIRECTION.UP;
		else if (request.floor<this.currentFloor)
			direction=DIRECTION.DOWN;
		else if (request.floor==this.currentFloor) {
			if (request.type==REQUEST_TYPE.FLOOR)
				direction=request.direction;
		}		
		return direction;
	}
	
	/**
	 * Decide a new moving direction after the current action. This method will change the elevator's moving direction.
	 * @param request - next request to answer
	 */
	private void decide_new_moving_direction(Request request) {
		this.movingDirection = caculate_direction_by_request(request);
	}
	
	/**
	 * This method will walk through the request list and return a string for print purpose
	 * @return - The current request list with format [Floor Requests: ...] [Rider Requests: ...]
	 */
	public String requestList_toString() {
		String fr = "", rr = "";
		for (int i=0; i<requestList.size(); i++) {
			Request r = requestList.get(i);
			if (r.type==REQUEST_TYPE.FLOOR)
				//fr = String.format("%s%d%c%s", fr,  r.floor, r.direction == DIRECTION.UP ? '^' : 'v', i == requestList.size()-1? "" : ", ");
				fr = String.format("%s%d%s", fr,  r.floor, i == requestList.size()-1? "" : ", ");
			else if (r.type==REQUEST_TYPE.RIDER)
				rr = rr + r.floor + ", ";
		}
		return String.format( "[Floor Requests: %s ] [Rider Requests: %s]", fr, rr);
	}	
	
	/**
	 * Calculate the distance of a request to the elevator based on the current elevator moving direction.
	 * This method includes the most important logic to sort request array.
	 * 
	 * @param request - The request to be calculated.
	 * @return - The number of floors elevator need to go to answer the request.
	 */
	public int distance(Request request) {
		if (request.type==REQUEST_TYPE.RIDER) { //Rider request. Means the request must have the SAME DIRECTION with current moving direction.
			return Math.abs(request.floor - this.currentFloor); 
		}
		else { //Floor request
			DIRECTION eleDirection = this.movingDirection;
			if (eleDirection == DIRECTION.NONE && this.requestList.size() > 0) {
				// Get intending direction.
				eleDirection = caculate_direction_by_request(this.requestList.get(0));
			}
			
			if (eleDirection == DIRECTION.NONE && this.requestList.size() == 0) {
				return Math.abs(request.floor - this.currentFloor);
			}
			
			if (eleDirection==DIRECTION.UP) { //Elevator is moving up
				if (request.direction==DIRECTION.UP) { //Floor request is to go up					
					if (request.floor>=this.currentFloor) { // Same direction ahead
						return request.floor - this.currentFloor;
					}
					else { // Same direction but behind. Note that this INCLUDE the request.floor==this.currentFloor case.
						return 2 * this.maxFloor - (this.currentFloor - request.floor); // Assume the elevator need to go to top then to bottom then answer the request
					}
				}
				else if (request.direction==DIRECTION.DOWN) { //Floor request is to go down
					return 2 * this.maxFloor - this.currentFloor - request.floor; // Assume the elevator need to go to top then answer the request
				}
			}
			else if (eleDirection==DIRECTION.DOWN) { //Elevator is moving down
				if (request.direction==DIRECTION.DOWN) { //Floor request is to go down
					if (request.floor<=this.currentFloor) { // Same direction ahead
						return this.currentFloor - request.floor;
					}
					else { // Same direction but behind. Note that this INCLUDE the request.floor==this.currentFloor case.
						return 2 * this.maxFloor + this.currentFloor - request.floor; // Assume the elevator need to go to bottom then to top then answer the request
					}
				}
				else if (request.direction==DIRECTION.UP) { // Floor request to go up
					return this.currentFloor + request.floor; // Assume the elevator need to go to bottom then answer the request
				}
			}
			//It is not possible for the elevator to be IDLE because that case has already been handled.
		}
		
		//Normally following code won't be reached...
		return Integer.MAX_VALUE;
	}
	
	/**
	 * Compare two requests determine which is nearer to current floor based on the current elevator moving direction.
	 * 
	 * @param r1 - Request 1.
	 * @param r2 - Request 2.
	 * @return - 1 if request 1 is nearer, 
	 * 		     0 if request 1 is same far as request 2.
	 * 			-1 if request 1 is farer.
	 */
	public int isNearer (Request r1, Request r2) {
		int d1 = distance(r1);
		int d2 = distance(r2);
		int result = (d1<d2) ? 1 : -1;
		if (d1 == d2 && d1 != Integer.MAX_VALUE) {
			result = 0; 
		}
		return result;
	}
	
	/**
	 * Add the request to the request list.
	 * 
	 *  @param request - The request to be added.
	 */
	public void addRequest (Request request) throws InvalidParameterException {
		if (request.type == REQUEST_TYPE.FLOOR) {			
			this.addFloorRequest(request.floor, request.direction);
		}
		else if (request.type == REQUEST_TYPE.RIDER) {
			this.addRiderRequest(request.floor);
		}
		else {
			throw new InvalidParameterException("Request type is incorrect!");
		}
	}
	
	/**
	 * Notify after the request list has been changed, such that 'wait' function in run function will stop waiting.  
	 * 
	 * @param checkRequestSize - true, then notify only when requestList.size() is larger than 0.
	 * 							 false, notify without checking the size of requestList.
	 */
	public void notifyRequestList(boolean checkRequestSize) 
	{
		synchronized(this.requestList) {//notify must operate the object with synchronized lock.			
			if (
				(checkRequestSize && this.requestList.size() > 0)
			|| !checkRequestSize		
				) 
			{
				//notify(): Wakes up a single thread that is waiting on this object's monitor.
				this.isBlocking = false;
				this.requestList.notify();
			}
		}
	}
	
	/**
	 * Notify by timeout action.
	 */
	public void notifyForTimeOut()  {
		notifyRequestList(false);
	}
	
	/**
	 * Add the floor request.
	 * 
	 * @param floor - The destination floor.
	 * @param direction - The direction of the request.	 
	 */
	public void addFloorRequest (int floor, DIRECTION direction) throws InvalidParameterException{

		if (floor<=0 || direction==DIRECTION.NONE || floor==1 && direction==DIRECTION.DOWN || floor==this.maxFloor && direction==DIRECTION.UP)
			throw new InvalidParameterException("Invalid Floor Request, please check your input! Your input: Floor="+floor+" Direction="+direction);
		
		synchronized(this.requestList) {				
			Request request = new Request(REQUEST_TYPE.FLOOR, floor, direction);
			
			if (this.movingDirection == DIRECTION.NONE && this.requestList.size()==0) { //Idling
				this.requestList.add(request);
			}
			else { //Moving
				int i;
				boolean alreadyExists = false;
				for (i=0; i<requestList.size(); i++) {
					Request request2 = this.requestList.get(i);
					int isNearer = isNearer(request, request2);
					if (isNearer == 0 && request.type == request2.type) {
						//Ignore the same request
						alreadyExists = true;
					}
					else if (isNearer == 1)
						break;
				}
				if (!alreadyExists)
					this.requestList.add(i,request);
			}
		}
		
		notifyRequestList(true);
	};

	/**
	 * Add the rider request.
	 * 
	 * @param floor - The destination floor of the rider request.
	 */
	public void addRiderRequest (int floor) throws InvalidParameterException{
		synchronized(this.requestList) {		
			if (floor<=0 || floor>this.maxFloor)
				throw new InvalidParameterException("The floor number must between 1 to "+this.maxFloor+". Your input is: "+floor);
			
			if (this.movingDirection==DIRECTION.UP && floor<=this.currentFloor ||
					this.movingDirection==DIRECTION.DOWN && floor>=this.currentFloor) { //Request need to be ignored				
				throw new InvalidParameterException("Elevator "+this.elevatorID+" Rider Request made for #"+floor+" floor - WRONG DIRECTION - Ignoring Request");
			}
			else 
			{
				Request request = new Request(REQUEST_TYPE.RIDER, floor, DIRECTION.NONE);
				boolean succeedToAdd = false;
				if (this.movingDirection == DIRECTION.NONE && this.requestList.size()==0) { //Idling
					this.requestList.add(request);
					succeedToAdd = true;
				}
				else { //Moving
					int i;
					boolean alreadyExists = false;
					for (i=0; i<requestList.size(); i++) {
						Request request2 = this.requestList.get(i);
						int isNearer = isNearer(request, request2);
						if (isNearer == 0 && request.type == request2.type) {
							//Ignore the same request. 
							alreadyExists = true;
						}
						else if (isNearer == 1)
							break;
					}
					if (!alreadyExists) {
						this.requestList.add(i,request);
						succeedToAdd = true;
					}
				}
				if (succeedToAdd) {
					Toolset.println("info", String.format("Elevator %d Rider Request made for Floor %d %s",
							getElevatorID(), floor, requestList_toString()));
				}
			}
		}
		
		notifyRequestList(true);
	};
	
	public void halt() {
		this.halt = true;
		synchronized(this.requestList) {
			// Wake up all threads that waiting on this object's monitor.
			this.isBlocking = false;
			this.requestList.notifyAll();
		}
	}
	
	public int getElevatorID()
	{
		return elevatorID;
	}
	
	
	public boolean isFull() {
		synchronized(this.currentRider) {
			if (this.currentRider.size() >= this.maxPassenger) {
				return true;
			}
			return false;
		}
	}
	
	public boolean isAvailableForRequest(Request request) {
		return isAvailableForRequest(request, false);
	}	
	
	/**
	 * Check if the elevator is available for the request.
	 * 
	 * @param request - The specific request.
	 * @param fromPendingList - 
	 * 		  1) Add as initial request if no request in the elevator request list. 
	 * 		  2) NextRequest.direction = InitialRequest.direction
	 *  	  3) NextRequest.floor - InitialRequest.floor = InitialRequest.direction.
	 * @return true  - This elevator can respond the request.
	 * 		   false - This elevator can't respond the request.
	 */
	public boolean isAvailableForRequest(Request request, boolean fromPendingList) {	
		synchronized(this.requestList) {
			if (this.isFull()){
					return false;
			}
			
			if (this.movingDirection==DIRECTION.NONE && this.requestList.size()==0) {//Idle
				return true;
			}
			else {//Has some requests.			
				if (this.requestList.size() > 0)
				{				
					DIRECTION eleDirection = this.movingDirection;
					Request firstRequest = this.requestList.get(0);
	
					if (firstRequest.type == REQUEST_TYPE.TIMEOUT) {//if current request is TIMEOUT, then unavailable for new request.
						return false;
					}
					
					if (eleDirection == DIRECTION.NONE ) {
						eleDirection = caculate_direction_by_request(firstRequest);
					}
	 
					if (eleDirection == DIRECTION.NONE ) {
						return false;
					}
					
					//*For direct floor request: Is the elevator moving towards the requesting floor?
					//*For pending list: 3) NextRequest.floor - InitialRequest.floor = InitialRequest.direction.
					if ( (eleDirection == DIRECTION.UP && request.floor < this.currentFloor ) 
					||   (eleDirection == DIRECTION.DOWN && request.floor > this.currentFloor) 
					  ){
						return false;
					}
					
					//*For direct floor request: Is the elevator moving in the same direction requested by the new floor request?
					//*For pending list: Does not require this condition.
					if (!fromPendingList) {
						if (eleDirection != request.direction) {
							return false;
						}
					}
					
					//*Is the direction of the elevators current request the same as the direction of this request?
					//*For pending list: 2) NextRequest.direction = InitialRequest.direction
					if (firstRequest.type == REQUEST_TYPE.FLOOR && firstRequest.direction != request.direction) {
						return false;
					}
				}
			}
			
			if (request.floor == this.lastPassedFloor) {// The requested floor has been passed(Door closed), although this.currentFloor is equal to request.floor.
				return false;
			}
			return true;
		}
	}
	
	/**
	 * Calculate waiting time for the request. Consider 2 factors:
	 * 1) Traveling time: Associated with distance.
	 * 2) Operation time: Open/close door for each stop.
	 * 
	 * @param request - The specific request.
	 * @return The calculated waiting time.
	 */
	public long calculateWaitingTimeForRequest(Request request) {		
		long traveling_time = Math.abs((distance(request) * this.timePerFloor));
		long operation_time = 0; // TODO		
		long waitingTime = traveling_time + operation_time;
//		Toolset.println("info", "ElevatorController -> "
//				+ "Elevator"+this.getElevatorID()+" currentFloor:" + this.currentFloor+" curDirection:"+this.movingDirection
//				+ " intendingWaitingTime:" +waitingTime
//				+" (E"+this.getElevatorID()+") ");

		return waitingTime;
	}
		
	public String riderListInfo() {
		String ret="[Riders: ";		
		synchronized(currentRider) {
			for(Person p : currentRider) {
				ret+=String.format("P%d%s", p.getPersonId(), p!=currentRider.get(currentRider.size()-1) ? ", " : "");
			}
		}
		ret+= "]";
		
		return ret;
	}
	
	public void addRider(Person person) {
		synchronized(currentRider) {
			currentRider.add(person);
			Toolset.println("info", String.format("Person P%d Entered Elevator %d %s", person.getPersonId(), this.getElevatorID(), riderListInfo()));			
		}
	}
		
	public void removeRider(Person person) {
		synchronized(currentRider) {
			currentRider.remove(person);
			Toolset.println("info", String.format("Person P%d has left Elevator %d %s", person.getPersonId(), this.getElevatorID(), riderListInfo()));			
		}
	}
	
	/**
	 * Operations when the elevator arrives at a new floor.
	 * 
	 * @param currentFloor - The floor which the elevator has arrived at.
	 */
	public void onArriveAtFloor(int currentFloor) {
		ArrayList<Floor> floorList = Building.getBuilding().getFloorsList();
			
		FloorImpl floor = null;	
		for (Floor f : floorList) {
			if (f.getFloorId() == currentFloor) {
				floor = (FloorImpl)f;
				break;
			}
		}
		
		if (floor == null) {
			throw new InvalidParameterException("Invalid parameter : currentFloor="+currentFloor+" floorListSize="+floorList.size());
		}
		
		String person_out = "";
		String person_in = "";
		
		//Person out
		synchronized(this.currentRider) {
			ArrayList<Person> outPerson = new ArrayList<Person>();
			for(int i=0;i < this.currentRider.size(); i++) {
				Person person = this.currentRider.get(i); 
				if (person.getToFloor() == currentFloor) {//Person arrived at the destination floor.
					person.endRiding();
					this.removeRider(person);
					i--;
					outPerson.add(person);
				}
			}
						
			if (outPerson.size() > 0) {
				floor.addTravelledPerson(outPerson);
//				person_out = "Elevator " + this.elevatorID+ " releases "+outPerson.size()+" persons on floor "+currentFloor+"."+"(Total passengers:"+this.currentRider.size()+")";
//				Toolset.println("info", person_out);
			}
		}
		
		//Person in
    	ArrayList<Person> removeWaitingList = new ArrayList<Person>();  
		ArrayList<Person> waitingList = floor.getWaitingList();        
        synchronized(waitingList)
        {
			for(int i=0;i < waitingList.size(); i++) 
			{
			    Person person = waitingList.get(i);		    		    
			    boolean willEnter = false;
			    if (this.requestList.size() == 0) {
			    	willEnter = true;
			    }
			    else {
			        Request firstRequest = this.requestList.get(0);
			        DIRECTION eleIntendingDirection = caculate_direction_by_request(firstRequest);
			        if (person.direction() == eleIntendingDirection) 
			        {
			            //1) riderRequest is same direction as elevator direction to next request.
			        	willEnter = true;
			            //2) riderRequest is same direction as next request if next request is floor request. (RIDER and TIMEOUT request doesn't has direction.)
//			            if( firstRequest.type==REQUEST_TYPE.FLOOR
//			               && person.direction() == firstRequest.direction) {
//			                //willEnter = false;
//			            }
			        }
			    }
			    
			    if (willEnter) 
			    {
			    	synchronized(this.currentRider) 
			    	{
						if (this.isFull()) {//Elevator is full, then add person's request to pending list.
							//printElevatorIsFull(Request.createWithPerson(person));
							Controller.getInstance().addPendingRequest(new Request(REQUEST_TYPE.FLOOR, person.getFromFloor(), person.direction()));
							break;
						}
						removeWaitingList.add(person);				        
			    	}
			    	
			    }
				
			}
			
			if (removeWaitingList.size() > 0) {
				floor.removePerson(removeWaitingList);				
				for (Person p : removeWaitingList) {
			        p.endWaiting();			        
			        synchronized(this.currentRider) {
						this.addRider(p);
					}			        
			        this.addRiderRequest(p.getToFloor());				        					
				}
			}
        }

		if (removeWaitingList.size() != 0) {
//			person_in = "Elevator " + this.elevatorID+ " loads "+removeWaitingList.size()+" persons on floor "+currentFloor+"."+"(Total passengers:"+this.currentRider.size()+")";
//			Toolset.println("info", person_in);
		}
		if (this.isFull())
			printElevatorIsFull(null);
	}
	
	/**
	 * Elevator is already full, request will be added to pending list.
	 * @param r - The new request that will be added to pending list.
	 */
	public void printElevatorIsFull(Request r) {
		//Toolset.println("info", String.format("Elevator %d is full, person's floor request is added to the pending list.(%s)", getElevatorID(), r.toInfoString() ));
		Toolset.println("info", String.format("Elevator %d is full, other persons' requests are added to the pending list.", getElevatorID() ));
	}
	
	/**
	 * Check if the elevator is idle.
	 * @return  true  - The elevator is idle.
	 * 			false - The elevator isn't idle.
	 */
	public boolean isIdle() {
		boolean isIdle = false;
		if (this.movingDirection==DIRECTION.NONE && this.requestList.size()==0)
			isIdle = true;
		return isIdle;
	}
	
	/**
	 * Check if the elevator is timeout.
	 * @return  true  - The elevator is timeout.
	 * 			false - The elevator isn't timeout.
	 */	
	public boolean isTimeOut()
	{
		boolean result = false;
		if (this.movingDirection==DIRECTION.NONE) {
			//if (this.requestList.size()==0) //requestList is blocked by wait() when requestList.size is equal to 0.
			if (this.isBlocking) {
				if (Toolset.getDeltaTimeLong()-this.lastDoorCloseTime>=this.timeOut) { 
					if (this.currentFloor!=this.defaultFloor){
						result = true;
					}
				}
			}
		}		
		return	result;
	}
		
	/**
	 * The elevator has finished all tasks and back to default floor.
	 * @return
	 */
	public boolean hasFinishedAllTasks() {
		boolean ret = false;		
		if (this.movingDirection==DIRECTION.NONE 
		 && this.isBlocking
	   	 && this.currentFloor == defaultFloor
	   	 ) {
			ret = true; 
		}
		return ret;
	}
	
	public int getMaxFloor() {
		return maxFloor;
	}
	
	public int timePerFloor() {
		return timePerFloor;
	}
	
	public int timePerDoorOp() {
		return timePerDoorOp;
	}		
	
	public DIRECTION lastFloorRequestDirection() {
		return lastFloorRequestDirection;
	}	
}
