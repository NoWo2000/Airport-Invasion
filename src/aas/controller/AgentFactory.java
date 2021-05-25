/**
 * 
 */
package aas.controller;

import java.util.Vector;
import java.util.logging.Logger;

import aas.AirportAgentSimulation;
import aas.model.Agent;
import aas.model.civil.Aircraft;
import aas.model.civil.CheckInCounter;
import aas.model.civil.pax.SimplePax;
import aas.model.communication.network.internet.InternetGateway;
import aas.model.communication.network.internet.InternetPort;
import aas.model.criminal.SimpleBraggart;
import aas.model.security.SecurityOperationsCenter;
import aas.model.security.SimpleOfficer;

/**
 * @author schier
 *
 */
public class AgentFactory {

	private static final Logger LOGGER = Logger.getLogger(AirportAgentSimulation.class.getName());
	
	private Vector<Agent> agents = new Vector<>();	
	
	/**
	 * 
	 */
	public AgentFactory() {
		super();
	}
	
	void builtAgents(AgentPattern[] patterns) {
		agents.clear();
		for(AgentPattern pattern : patterns) {
			Agent agent = this.builtAgent(pattern);
			if(agent == null) {
				throw new NullPointerException("agent at position " + this.agents.size() + " initilizes with null");
			}
			agents.add(agent);
		}
	}
	
	//TODO Integrate gateway!!!
	Agent builtAgent(AgentPattern pattern) {
		switch(pattern.getAgentClass()) {
			case "simplepax":	return new SimplePax(this.generateId(), pattern.getAgentName(), pattern.getStart(), pattern.getParameter("flight"));
			case "checkin": 	return new CheckInCounter(this.generateId(), pattern.getAgentName(), pattern.getStart());
			case "aircraft": 	return this.builtAircraft(pattern);
			case "simpleofficer": 	return new SimpleOfficer(this.generateId(), pattern.getAgentName(), pattern.getStart());
			case "securityoperationscenter": 	return new SecurityOperationsCenter(this.generateId(), pattern.getAgentName(), pattern.getStart());
			case "braggart": 	return new SimpleBraggart(this.generateId(), pattern.getAgentName(), pattern.getStart());
		default : 
			LOGGER.severe("Unkown agent class " + pattern.getAgentClass());
			return null;
		}
	}
	
	private Agent builtAircraft(AgentPattern pattern) {
		if(!pattern.hasParameter("seats")) {
			LOGGER.severe("Tried to initialize aircraft, but no seat parameter defined.");
			return null;
		}
		
		Aircraft aircraft = new Aircraft(this.generateId()
				, pattern.getStart()
				, pattern.getAgentName()
				, Integer.valueOf(pattern.getParameter("seats")));
		
		if(pattern.hasParameter("gateway")) {
			String gatewayName = pattern.getParameter("gateway");
			Integer gateway = this.getAgent(gatewayName);
			if(gateway != null) {
				aircraft.setGateway(gateway);
			} else {
				LOGGER.warning("Gateway " + gatewayName + " could not be resolved");
			}
		} else {
			LOGGER.warning("aircraft " + aircraft.getFootprint().getName() + " initialized without gateway.");
		}
		
		return aircraft; 
	}
	
	private Integer getAgent(String name) {
		for(Agent agent : this.agents) {
			if(agent.getFootprint().getName().compareTo(name) == 0) {
				return agent.getFootprint().getId();
			}
		}
		return null;
	}
	
	public Agent[] getAgents() {
		return this.agents.toArray(new Agent[agents.size()]);
	}
	
	private int generateId() {
		return this.agents.size();
	}
	

}
