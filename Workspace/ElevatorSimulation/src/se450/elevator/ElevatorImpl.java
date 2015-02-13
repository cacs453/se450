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
	//private boolean isMoving;
	private DIRECTION movingDirection;
	protected int currentFloor;
	private ArrayList<Person> currentRider;
	private ArrayList<Request> requestList;
	//private ElevatorButtonPanel buttonPanel;
	private boolean halt;
	private long checkInterval = 100; //Sleep interval between each time elevator check its status
	private long lastFloorPassingTime;
	private boolean hasPrinted = false;

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
				if (this.movingDirection==DIRECTION.NONE) { //Idling
					if (this.requestList.size()==0) { //No task
						if (Toolset.getDeltaTimeLong()-this.lastDoorCloseTime>=this.timeOut) { //Time out, go to default floor
							if (this.currentFloor!=this.defaultFloor)
								this.requestList.add(new Request(REQUEST_TYPE.TIMEOUT, this.defaultFloor, DIRECTION.NONE));
						}
						else { //Continue idling
							Toolset.println("debug", "Elevator "+this.elevatorID+" is idling.");
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
					//Operation on person list - cheng
					
					if (this.currentFloor==request.floor) { //Arrived at the requested floor
						
						//Remove the request(s) from the list
						while (this.requestList.size()>0 && this.requestList.get(0).floor==this.currentFloor)
							this.requestList.remove(0);
						
						if (request.type==REQUEST_TYPE.TIMEOUT) { //Arrived default floor because time out
							Toolset.println("info", "Elevator "+this.elevatorID+" has arrived at Default Floor after Timeout");
						}
						else {
							//Open/close the door
							String msg = "Elevator "+this.elevatorID+" has arrived at Floor "+this.currentFloor+" for ";
							if (request.type==REQUEST_TYPE.FLOOR)
								msg = msg + request.direction + " request";
							else
								msg = msg + "Rider Request";
							Toolset.println("info", msg);
							Toolset.println("info", "Elevator "+this.elevatorID+" Doors Open");
							Thread.sleep(this.timePerDoorOp);
							Toolset.println("info", "Elevator "+this.elevatorID+" Doors Close");
							this.lastDoorCloseTime = Toolset.getDeltaTimeLong();
							
							//Operation on person list - cheng
							
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
							String msg = "Elevator "+this.elevatorID+" moving from Floor "+this.currentFloor+" to Floor ";
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

				Thread.sleep(this.checkInterval);
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		Toolset.println("debug", "Elevator "+this.elevatorID+" exists per request.");
	}
	
	/**
	 * Decide a new moving direction after the current action. This method will change the elevator's moving direction.
	 * @param request - next request to answer
	 */
	private void decide_new_moving_direction(Request request) {
		if (request.floor>this.currentFloor)
			this.movingDirection=DIRECTION.UP;
		else if (request.floor<this.currentFloor)
			this.movingDirection=DIRECTION.DOWN;
		else if (request.floor==this.currentFloor) {
			if (request.type==REQUEST_TYPE.FLOOR)
				this.movingDirection=request.direction;
		}
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
				fr = fr + r.floor + ", ";
			else if (r.type==REQUEST_TYPE.RIDER)
				rr = rr + r.floor + ", ";
		}
		return "[Floor Requests: "+fr+"] [Rider Requests: "+rr+"]";
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
			if (this.movingDirection==DIRECTION.UP) { //Elevator is moving up
				if (request.direction==DIRECTION.UP) { //Floor request is to go up
					if (request.floor>this.currentFloor) { // Same direction ahead
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
			else if (this .movingDirection==DIRECTION.DOWN) { //Elevator is moving down
				if (request.direction==DIRECTION.DOWN) { //Floor request is to go down
					if (request.floor<this.currentFloor) { // Same direction ahead
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
	 * @return - True if request 1 is nearer, False if request 1 is farer.
	 */
	public boolean isNearer (Request r1, Request r2) {
		int d1 = distance(r1);
		int d2 = distance(r2);
		return (d1<=d2) ? true : false;
	}
	
	public void addFloorRequest (int floor, DIRECTION direction) throws InvalidParameterException{

		if (floor<=0 || direction==DIRECTION.NONE || floor==1 && direction==DIRECTION.DOWN || floor==this.maxFloor && direction==DIRECTION.UP)
			throw new InvalidParameterException("Invalid Floor Request, please check your input! Your input: Floor="+floor+" Direction="+direction);
		
		Request request = new Request(REQUEST_TYPE.FLOOR, floor, direction);
		
		if (this.movingDirection == DIRECTION.NONE && this.requestList.size()==0) { //Idling
			this.requestList.add(request);
		}
		else { //Moving
			int i;
			for (i=0; i<requestList.size(); i++)
				//Ignore the same request, but still add related person to currentRider - cheng
				if (isNearer(request, this.requestList.get(i)))
					break;
			this.requestList.add(i,request);
		}

	};

	public void addRiderRequest (int floor) throws InvalidParameterException{
		
		if (floor<=0 || floor>this.maxFloor)
			throw new InvalidParameterException("The floor number must between 1 to "+this.maxFloor+". Your input is: "+floor);
		
		if (this.movingDirection==DIRECTION.UP && floor<=this.currentFloor ||
				this.movingDirection==DIRECTION.DOWN && floor>=this.currentFloor) { //Request need to be ignored
			Toolset.println("info", "Elevator "+this.elevatorID+" Rider Request made for #"+floor+" floor - WRONG DIRECTION - Ignoring Request");
		}
		else {
			Request request = new Request(REQUEST_TYPE.RIDER, floor, DIRECTION.NONE);
			if (this.movingDirection == DIRECTION.NONE && this.requestList.size()==0) { //Idling
				this.requestList.add(request);
			}
			else { //Moving
				int i;
				for (i=0; i<requestList.size(); i++)
					if (isNearer(request, this.requestList.get(i)))
						break;
				this.requestList.add(i,request);
			}
		}
	};
	
	public void halt() {
		this.halt = true;
	}
	
	public int getElevatorID()
	{
		return elevatorID;
	}
}
