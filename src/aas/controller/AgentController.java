/*COPYRIGHT**************************************************************************************
* Copyright (C) 2021                                                                        *
* DLR Deutsches Zentrum fuer Luft- und Raumfahrt e.V./German Aerospace Center e.V.              *
* Institut fuer Flugfuehrung/Institute of Flight Guidance                                       *
* Tel. +49 531 295 2500, Fax: +49 531 295 2550                                                  *
* WWW: http://www.dlr.de/fl/                                                                    *
*                                                                                               *
* These coded instructions, statements, and computer programs contain unpublished proprietary   *
* information of the German Aerospace Center e.V. and are protected by copyright law. They may  *
* not be disclosed to third parties or copied or duplicated in any form, in whole or in part,   *
* without the prior written consent of the German Aerospace Center e.V.                         *
***********************************************************************************END_COPYRIGHT*/

package aas.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import aas.controller.export.Export;
import aas.controller.logger.LoggerController;
import aas.controller.logger.LoggerType;
import aas.model.Agent;
import aas.model.AgentFootprint;
import aas.model.communication.Message;
import aas.model.util.Point;

/**
 * Controller that runs the agents
 * @author schier
 *
 */
@Root
public class AgentController {
	
	private static final Logger EVENT_LOGGER = Logger.getLogger(LoggerType.EVENT + ".controller");
	private static final Logger LOGGER = Logger.getLogger(AgentController.class.getName());
	
		
	private AgentPattern[] patterns;
	private HashMap<Integer, Agent> agents = new HashMap<>();
	private Vector<Export> exports = new Vector<>();
	
	private CommunicationController comController = new CommunicationController(this);
	private LoggerController loggerController = new LoggerController();
	
	private Double neighbourhoodDistance = 13.0;
	private Integer simulationCycles = 100;
	
	/**
	 * Constructor
	 * @param patterns - the agent patterns to feed the agent factory
	 */
	public AgentController(@ElementArray(name="agents") AgentPattern[] patterns) {
        super();
        this.setAgents(patterns);
    }
	
	/**
	 * Constructor
	 * @param patterns - the agent patterns to feed the agent factory
	 */
	public AgentController(@ElementArray(name="agents") AgentPattern[] patterns
			, @Element(name="neighbourhood-distance", required=false) double neighbourhood
			, @Element(name="simulation-cycles", required=false) int simSteps) {
        super();			 
        this.setAgents(patterns);
        this.setNeighbourhoodDistance(neighbourhood);
        this.setSimulationCycles(simSteps);
    }
	
	/**
	 * Constructor
	 * Mainly for Unit Test purposes
	 */
	public AgentController() {
		super();
	}

	/**
	 * Getter for xml unmarshalling
	 * @return agent patterns
	 */
    @ElementArray(name="agents")
    protected AgentPattern[] getAgents() {
        return patterns;
    }
	
    /**
     * Adds an agent to the simulation
     * @param agent - the agent to add
     */
	public void add(final Agent agent) {
		if(agent == null) {
			throw new IllegalArgumentException("Unable to add null agent");
		}
		
		if(agents.containsKey(agent.getFootprint().getId())) {
			throw new IllegalArgumentException("Unable to add agent with " + agent.getFootprint().getId() + " - Id already exists.");
		}
		
		
		this.agents.put(agent.getFootprint().getId(),agent);
	}
	
	/**
	 * Setter
	 * @param newExports - the exports to use
	 * TODO: Enable XML unmarshalling for exports
	 */
	/*@ElementUnion({
	      @Element(name="geojson", type=Circle.class),
	      @Element(name="rectangle", type=Rectangle.class)
	   })*/
	public void setExports(Export[] newExports) {
		this.exports.clear();
		this.exports.addAll(Arrays.asList(newExports));
	}
	
	/**
	 * Getter for xml unmarshalling
	 * @return the exports
	 */
	/*@ElementArray(name="exports")*/
	protected Export[] getExports() {
		return this.exports.toArray(new Export[this.exports.size()]);
	}
	
	/**
	 * Setter
	 * @param newAgents - all agents
	 */
	private void setAgents(AgentPattern[] newAgents) {
		this.patterns = newAgents;
		this.agents.clear();
		AgentFactory factory = new AgentFactory();
		factory.builtAgents(newAgents);
		for(Agent agent : factory.getAgents()) {
			this.add(agent);
		}
	}
	
	/**
	 * Removes all given agents
	 * @param agents - the agents to remove
	 */
	private void removeAllAgents(List<Agent> agents) {
		for(Agent agent : agents) {
			this.agents.remove(agent.getFootprint().getId());
		}
	}
	
	/**
	 * Removes an agent
	 * @param agent - the agent to remove
	 */
	public void remove(Agent agent) {
		if((agent == null) || (!agents.containsKey(agent.getFootprint().getId()))) {
			throw new IllegalArgumentException("Unable to remove agent");
		}
		this.agents.remove(agent);
	}
	
	/**
	 * Runs the simulation with the iven number of cycles
	 * @param cycles - the number of cycles to calculate
	 */
	public void run() {
		for(int index = 0; index < this.simulationCycles; index++) {
			EVENT_LOGGER.info("=== Simulation Step " + index + " ===");
			Vector<Agent> finishedAgents = new Vector<>();
			for(Agent agent : agents.values()) {
				int agentId = agent.getFootprint().getId();
				Message[] incomingMessages = comController.getOutgoing(agentId); 
				AgentFootprint[] neighbours = this.getNeighbours(agent);
				Message[] outgoingMessages = agent.simulate(index, incomingMessages , neighbours);
				comController.addMessages(outgoingMessages);

				if(agent.isDone()) {
					EVENT_LOGGER.info(index +  " - controller: Removing agent " + agent.getFootprint());
					finishedAgents.add(agent);
				}
				this.notifyExports(index, agent, outgoingMessages);
			}
			comController.transfer(index);
			this.removeAllAgents(finishedAgents);
			finishedAgents.clear();
			EVENT_LOGGER.info(index + " - controller: " + this.agents.size() + " agents in simulation." );
		}
		
		this.finishExports();
	}
	
	/**
	 * Notifies all exports listening
	 * @param time - the time of the notification
	 * @param agentName - the agent name
	 * @param position - the position of the agent
	 * @param message - the messages generated by the agent
	 */
	private void notifyExports(int time, Agent agent, Message[] message) {
		for(Export export : this.exports) {
			export.notify(time, agent, message);
		}
	}
	
	/**
	 * Notify all exports that simulaiton has ended (for file generation)
	 */
	private void finishExports() {
		for(Export export : this.exports) {
			export.finish();
		}
	}
	
	/**
	 * Getter
	 * @param agentClass - the agent class
	 * @return all agents of the given class
	 */
	public AgentFootprint[] getFootprints(Point source, double distance) {
		Vector<AgentFootprint> footprints = new Vector<>();
		for(Agent agent : this.agents.values()) {
				if(agent.getFootprint().getPosition().getDistance(source) <= distance) {
					footprints.add(agent.getFootprint());
				}
		}
		
		return footprints.toArray(new AgentFootprint[footprints.size()]);
	}
	
	/**
	 * Getter
	 * @param id - the agent id
	 * @return the agent's footprint
	 */
	public AgentFootprint getAgent(int id) {
		return this.agents.get(id).getFootprint();
	}

	/**
	 * Getter
	 * @param agent - the focused agent
	 * @return all agents in the neighbourhood
	 */
	private AgentFootprint[] getNeighbours(Agent agent) {
		Vector<AgentFootprint> neighbours = new Vector<>();
		for(Agent possibleNeighbour : this.agents.values()) {
			if(possibleNeighbour.getFootprint().getId() == agent.getFootprint().getId()) {
				continue;
			}
			
			Point p1 = agent.getFootprint().getPosition();
			Point p2 = possibleNeighbour.getFootprint().getPosition();
			if(p1.getDistance(p2) < neighbourhoodDistance) {
				neighbours.add(possibleNeighbour.getFootprint());
			}
		}
		return neighbours.toArray(new AgentFootprint[neighbours.size()]);
	}

	/**
	 * Getter for the distance in which a agent is accessible as neighbour
	 * @return the distance in meters 
	 */
	@Element(name="neighbourhood-distance", required=false)
	public double getNeighbourhoodDistance() {
		return neighbourhoodDistance;
	}

	public void setNeighbourhoodDistance(Double neighbourhoodDistance) {
		this.neighbourhoodDistance = neighbourhoodDistance;
	}

	/**
	 * Getter
	 * @return the number of simulations cycles to perform
	 */
	@Element(name="simulation-cycles", required=false)
	public Integer getSimulationCycles() {
		return simulationCycles;
	}

	public void setSimulationCycles(Integer simulationCycles) {
		this.simulationCycles = simulationCycles;
	}
	
	
	
}
