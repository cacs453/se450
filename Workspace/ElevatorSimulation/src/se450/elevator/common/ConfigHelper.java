package se450.elevator.common;

import java.util.ArrayList;

import se450.elevator.ElevatorFactory;
import se450.elevator.ElevatorImpl;
import se450.elevator.PanelRequest;
import se450.elevator.Elevator;
import se450.elevator.Person;

import javax.xml.parsers.*;  

import org.w3c.dom.*; 

import se450.elevator.PersonImpl;

/**
 * A helper class to get the configurations from xml file.
 *
 */
public class ConfigHelper {

	private static int floorNumbers = 0;
	private static int elevatorNumbers = 0;
	
	private static final ArrayList<Elevator> elevatorList = new ArrayList<Elevator>();
	private static final ArrayList<Person> personList = new ArrayList<Person>();
	private static final ArrayList<PanelRequest> panelRequestList = new ArrayList<PanelRequest>();
	
	/**
	 * Get all of the settings from xml file
	 * 
	 */
	private static void GetConfig()
	{		
        try  
        {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
            DocumentBuilder db = dbf.newDocumentBuilder();  
            Document doc = db.parse("./src/se450/elevator/config/config.xml");  
  
            //floor number
            floorNumbers = Integer.parseInt(doc.getElementsByTagName("floornumbers").item(0).getFirstChild().getNodeValue());
            elevatorNumbers = Integer.parseInt(doc.getElementsByTagName("elevatornumbers").item(0).getFirstChild().getNodeValue());
            
            //elevator list
            NodeList elevatorNodeList = doc.getElementsByTagName("elevator");
            for (int i = 0; i < elevatorNodeList.getLength(); i++)  
            {  
            	int elevatorID = 0;
            	int maxPassenger = 0;
            	int timePerFloor = 0;
            	int timePerDoor = 0;
            	int defaultFloor = 0;
            	int timeout = 0;
            	
                Node elevatorNode = elevatorNodeList.item(i);  
                
                for (Node node = elevatorNode.getFirstChild(); node != null; node = node.getNextSibling())  
                {  
                    if (node.getNodeType() == Node.ELEMENT_NODE)  
                    {  
                        String name = node.getNodeName(); 
                        String value = node.getFirstChild().getNodeValue();
                        switch(name)
                        {
                        	case "id": elevatorID = Integer.parseInt(value);break;
                        	case "maxpassengers": maxPassenger = Integer.parseInt(value);break;
                        	case "timeperfloor": timePerFloor = Integer.parseInt(value);break;
                        	case "timeperdoor": timePerDoor = Integer.parseInt(value);break;
                        	case "defaultfloor": defaultFloor = Integer.parseInt(value);break;
                        	case "timeout": timeout = Integer.parseInt(value);break;
                        	default: break;                        
                        }
                        //System.out.println(name + ":" + value + "\t");  
                    }
                }
                ElevatorImpl elevator = ElevatorFactory.createElevator(maxPassenger, floorNumbers, timePerFloor, timePerDoor, defaultFloor, timeout);
                elevatorList.add(elevator);
            }  
            
            //person list
            NodeList personNodeList = doc.getElementsByTagName("person");
            for (int i = 0; i < personNodeList.getLength(); i++)  
            {
            	int id = 0;
            	int from = 0;
            	int to = 0;
            	long triggertime = 0;
            	
                Node elevatorNode = personNodeList.item(i);  
                
                for (Node node = elevatorNode.getFirstChild(); node != null; node = node.getNextSibling())  
                {  
                    if (node.getNodeType() == Node.ELEMENT_NODE)  
                    {                    	
                        String name = node.getNodeName(); 
                        String value = node.getFirstChild().getNodeValue();
                        switch(name)
                        {
                        	case "id": id = Integer.parseInt(value);break;
                        	case "from": from = Integer.parseInt(value);break;
                        	case "to": to = Integer.parseInt(value);break;
                        	case "triggertime": triggertime = Long.parseLong(value);break;
                        	default: break;                        
                        }
                        //System.out.println(name + ":" + value + "\t");  
                    }
                }
                PersonImpl person = new PersonImpl(id, from, to, triggertime);
                personList.add(person);
            } 
            
           //panel request list
            NodeList panelNodeList = doc.getElementsByTagName("panelrequest");
            for (int i = 0; i < panelNodeList.getLength(); i++)  
            {
            	int elevatorid = 0;
            	int floorid = 0;
            	long triggertime = 0;
            	
                Node panelNode = panelNodeList.item(i);  
                
                for (Node node = panelNode.getFirstChild(); node != null; node = node.getNextSibling())  
                {  
                    if (node.getNodeType() == Node.ELEMENT_NODE)  
                    {                    	
                        String name = node.getNodeName(); 
                        String value = node.getFirstChild().getNodeValue();
                        switch(name)
                        {
                        	case "elevatorid": elevatorid = Integer.parseInt(value);break;
                        	case "floorid": floorid = Integer.parseInt(value);break;
                        	case "triggertime": triggertime = Long.parseLong(value);break;
                        	default: break;                        
                        }
                        //System.out.println(name + ":" + value + "\t");  
                    }
                }
                PanelRequest panelRequest = new PanelRequest(elevatorid, floorid, triggertime);
                panelRequestList.add(panelRequest);
            } 
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }        
	}
	
	/**
	 * Get floor numbers of the building
	 * 
	 */
	public static int getFloorNumbers() {
		if (floorNumbers==0)
			GetConfig();
		
		return floorNumbers;
	}
	
	/**
	 * Get elevator numbers of the building
	 * 
	 */
	public static int getElevatorNumbers() {
		if (elevatorNumbers==0)
			GetConfig();
		
		return elevatorNumbers;
	}
	
	/**
	 * Get all of the elevator list, including their attributes
	 * 
	 */
	public static ArrayList<Elevator> getElevatorList() {
		if (elevatorList.size()==0)
			GetConfig();
		
		return elevatorList;
	}
	
	/**
	 * Get all of the person list, including their attributes
	 * 
	 */
	public static ArrayList<Person> getPersonList() {
		if (personList.size()==0)
			GetConfig();
		
		return personList;
	}
	
	/**
	 * Get all of the dummy requests inside the elevator
	 * 
	 */
	public static ArrayList<PanelRequest> getPanelRequestList() {
		if (panelRequestList.size()==0)
			GetConfig();
		
		return panelRequestList;
	}
    
}
