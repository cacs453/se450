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
 * @author Rong Zhuang
 */
public final class ConfigHelper {

	private static final ConfigDTO configDTO = new ConfigDTO();
	
	/**
	 * Make the constructor private to avoid being instantiating.
	 */
	private ConfigHelper() {
	
	}
	
	/**
	 * Get all of the settings from xml file and save to ConfigDTO
	 * 
	 */
	public static ConfigDTO getConfigDTO()
	{		
        try  
        {
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
            DocumentBuilder db = dbf.newDocumentBuilder();  
            Document doc = db.parse("./src/se450/elevator/config/config.xml");  
  
            //basic settings
            configDTO.setFloorNumbers(Integer.parseInt(GetBuildingAttributes(doc, "floornumbers")));
            configDTO.setElevatorNumbers(Integer.parseInt(GetBuildingAttributes(doc, "elevatornumbers")));
            configDTO.setRandomPersonNumbers(Integer.parseInt(GetBuildingAttributes(doc, "randompersonnumbers")));
            configDTO.setSimulationDuration(Long.parseLong(GetBuildingAttributes(doc, "simulationduration")));
            
            //elevator list
            configDTO.setElevatorList(getElevatorList(doc, "elevator"));
            
           //person list
            configDTO.setPersonList(getPersonList(doc, "person"));
            
            //panel request list
            configDTO.setPanelRequestList(getPanelRequestList(doc, "panelrequest"));            
            
            return configDTO;
            
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
            return null;
        }        
	}
	
	/**
	 * 
	 * @param doc - Xml file
	 * @param node - Node name
	 * @return string - Node value
	 */
	private static String GetBuildingAttributes(Document doc, String node)	{
		return doc.getElementsByTagName(node).item(0).getFirstChild().getNodeValue();
	}

	/**
	 * @param doc - Xml file
	 * @param tag - Tag name
	 * @return Arraylist - Elevator List
	 */
	private static ArrayList<Elevator> getElevatorList(Document doc, String tag) {
		
		ArrayList<Elevator> elevatorList = new ArrayList<Elevator>();
		
        NodeList elevatorNodeList = doc.getElementsByTagName(tag);
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
                }
            }
            ElevatorImpl elevator = ElevatorFactory.createElevator(maxPassenger, configDTO.getFloorNumbers(), timePerFloor, timePerDoor, defaultFloor, timeout);
            elevatorList.add(elevator);
        }  
        
		return elevatorList;
	}
	
	/**
	 * @param doc - Xml file
	 * @param tag - Tag name
	 * @return Arraylist - Person List
	 */
	private static ArrayList<Person> getPersonList(Document doc, String tag) {
		
		ArrayList<Person> personList = new ArrayList<Person>();
		
        NodeList personNodeList = doc.getElementsByTagName(tag);
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
                }
            }
            PersonImpl person = new PersonImpl(id, from, to, triggertime);
            personList.add(person);            
        } 
        
        return personList;
	}
	
	/**
	 * @param doc - Xml file
	 * @param tag - Tag name
	 * @return Arraylist - Panel Request List
	 */
	private static ArrayList<PanelRequest> getPanelRequestList(Document doc, String tag) {
		
		ArrayList<PanelRequest> panelRequestList = new ArrayList<PanelRequest>();
		
		NodeList panelNodeList = doc.getElementsByTagName(tag);
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
                }
            }
            PanelRequest panelRequest = new PanelRequest(elevatorid, floorid, triggertime);
            panelRequestList.add(panelRequest);
        } 
        
        return panelRequestList;
	}	
    
}
