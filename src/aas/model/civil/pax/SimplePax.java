package aas.model.civil.pax;


import java.util.logging.Logger;

import aas.controller.logger.LoggerType;
import aas.model.Agent;
import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.Moveable;
import aas.model.communication.Message;
import aas.model.util.Point;

/**
 * Simple Passanger agent that walks randomly around until a checkin is his neighbour.
 * As soon as he finds the checkin he requests a ticket for his flight and walks straiht to the gate
 * If he is 5km away from his starting point he quits his walk.  
 * @author schier
 *
 */
public class SimplePax implements Agent, Moveable {
	
	private final static Logger LOGGER = Logger.getLogger( LoggerType.EVENT + ".simplePax" );
	
	private static final double MAX_TRAVEL_DISTANCE = 5000.0;
	
	private AgentFootprint footprint;
	private Point startPosition = new Point();
	private Point lastPosition = new Point();
	private Point currentPosition = new Point();
	private State state;
	
	
	/**
	 * Constructor
	 * @param id - the agent id
	 * @param name - the agents name
	 * @param position - the initial position
	 * @param flight - the flight the passenger likes to take
	 */
	public SimplePax(int id, String name, Point position, String flight) {
		super();
		this.startPosition = position;
		this.currentPosition = position;
		this.footprint = new AgentFootprint(id, AgentRole.Civil, "pax", name, this.currentPosition);
		this.setState(0L, new SearchCheckIn(this.currentPosition, flight));
	}

	/**
	 * Setter
	 * @param newPosition - the new position to set
	 */
	private void setPosition(Point newPosition) {
		this.lastPosition = this.currentPosition;
		this.currentPosition = newPosition;
		this.footprint.setPosition(currentPosition);
	}

	/**
	 * Simulates the next time step
	 */
	@Override
	public Message[] simulate(int time, Message[] messages, AgentFootprint[] neighbours) {
		Point newPosition = state.calculateNextStep(time, messages, neighbours);
		Message[] newRequests = state.getRequests(time, this.getFootprint()); 
		this.setPosition(newPosition);
		
		if(state.getNextState() != null) {
			this.setState(time, state.getNextState());
		}
		return newRequests;
	}
	
	
	/**
	 * Setter
	 * @param time - the current time
	 * @param newState - the new state
	 */
	private void setState(long time, State newState) {
		if(newState == null) {
			throw new IllegalArgumentException("state for agent must not be null");
		}
		
		if(this.state == null) {
			LOGGER.info(Long.toString(time) + " - "
				+ this.footprint.toString() + ": " 
				+ " is initially in state " + newState.getClass().getSimpleName());
		} else {
			LOGGER.info(Long.toString(time) + " - "
				+ this.footprint.toString() + ": " 
				+ this.state.getClass().getSimpleName()+ "->" + newState.getClass().getSimpleName());
		}
		
		this.state = newState;
	}
	
	

	/**
	 * Getter
	 * @return the speed in meters per time step
	 */
	@Override
	public double getSpeed() {
		return lastPosition.getDistance(currentPosition);
	}

	/**
	 * Getter
	 * @return the direction in deg
	 */
	@Override
	public double getDirection() {
		return lastPosition.getDirection(currentPosition);
	}

	/**
	 * Getter
	 * @return the footprint with the current position
	 */
	@Override
	public AgentFootprint getFootprint() {
		this.footprint.setPosition(currentPosition);
		return this.footprint;
	}

	/**
	 * Check
	 * @return true if agent is boarded or 5km away from his inital position
	 */
	@Override
	public boolean isDone() {
		return (this.state instanceof Boarded) || (this.currentPosition.getDistance(this.startPosition) > MAX_TRAVEL_DISTANCE);
	}
	
}
