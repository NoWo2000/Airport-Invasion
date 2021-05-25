/**
 * 
 */
package aas.model.security;

import java.util.Vector;
import java.util.logging.Logger;

import aas.controller.logger.LoggerType;
import aas.model.Agent;
import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.Moveable;
import aas.model.communication.Message;
import aas.model.communication.manual.ArrestCall;
import aas.model.communication.network.radio.PoliceCode;
import aas.model.communication.network.radio.PoliceRadio;
import aas.model.util.Point;

/**
 * Simulates a simple officer holding position until he identfies a criminal
 * in his neighbourhood or gets a call from his operations center
 * @author schier
 *
 */
public class SimpleOfficer implements Agent, Moveable {
	
	private static final Logger EVENT_LOGGER = Logger.getLogger(LoggerType.EVENT + ".officer");
	
	private static final Double MAX_CHASE_SPEED = 10.0;
	
	private AgentFootprint footprint;
	private Point currentPosition;
	private Point lastPosition;
	private Point target;

	/**
	 * Constructor
	 * @param id - the id of the agent
	 * @param name - the agent's name
	 * @param start - the starting point
	 */
	public SimpleOfficer(int id, String name, Point start) {
		this.footprint = new AgentFootprint(id, AgentRole.Security, "simpleOfficer", name, start);
		this.currentPosition = start;
	}

	/**
	 * Getter
	 * @return the speed in meters per sim step
	 */
	@Override
	public double getSpeed() {
		if(lastPosition == null) {
			return 0;
		}
		return lastPosition.getDistance(this.currentPosition);
	}

	/**
	 * Getter
	 * @return the direction in degrees
	 */
	@Override
	public double getDirection() {
		if(lastPosition == null) {
			return 0;
		}
		return lastPosition.getDirection(this.currentPosition);
	}

	/**
	 * Getter
	 * @return the footprint
	 */
	@Override
	public AgentFootprint getFootprint() {
		this.footprint.setPosition(currentPosition);
		return this.footprint;
	}

	/**
	 * Simulates the officer.
	 * 1. Check incoming messages for SOC message
	 * 2. Check neighbourhood for criminals and send arrest messages if detected
	 * 3. If target given (criminal detected or SOC chase message) go to the target
	 */
	@Override
	public Message[] simulate(int time, Message[] messages, AgentFootprint[] neighbours) {
		this.lastPosition = this.currentPosition;
		Vector<Message> output = new Vector<>();
		
		this.checkMessages(time, messages);
		
		//1. Criminal detected
		AgentFootprint criminal = this.checkNeighbours(neighbours);
		if((target == null) && (criminal != null)) {
			this.target = criminal.getPosition();
			PoliceRadio radioCall = new PoliceRadio(time, this.getFootprint().getId(), PoliceCode.CriminalDetected, criminal);
			output.add(radioCall); 
			EVENT_LOGGER.info(time + " - " + this.getFootprint().toString() + ": Detected criminal with id " + criminal.getId());
		}
		
		//2. Chase criminal
		if(target != null) {
			//2.1 Update criminal position
			if(criminal != null) {
				this.target = criminal.getPosition();
			}
			
			//2.2 Walk to position
			this.currentPosition = this.chaseCriminal();
			
			//2.3 arrest if possible
			Message arrest = this.arrestCriminal(time, criminal);
			if(arrest != null) {
				output.add(arrest);
				output.add(new PoliceRadio(time, this.getFootprint().getId(), PoliceCode.CriminalArrested, criminal));
				EVENT_LOGGER.info(time + " - " + this.getFootprint().toString() + ": Arresting criminal with id " + criminal.getId());
			}
		}
		
		return output.toArray(new Message[output.size()]);
	}
	
	/**
	 * Check Messages and set target due to chase message
	 * @param time - the time of the check
	 * @param messages - the messages to check
	 */
	private void checkMessages(long time, Message[] messages) {
		for(Message message : messages) {
			if(message instanceof PoliceRadio) {
				PoliceRadio radio = (PoliceRadio) message;
				switch(radio.getCode()) {
					case ChaseCriminal: 
						this.target = radio.getCriminal().getPosition(); 
						EVENT_LOGGER.info(time + " - " + this.getFootprint().toString() + ": Chasing criminal with id " + radio.getCriminal().getId());
						break;
					case StopChase: 
						this.target = null;
						EVENT_LOGGER.info(time + " - " + this.getFootprint().toString() + ": Stop chasing. Holding position.");
						break;
				default: continue;	
				}
			}
		}
	}
	
	/**
	 * Calculate walking to target
	 * @return the new position
	 */
	private Point chaseCriminal() {
		return this.currentPosition.moveTo(this.currentPosition, target, MAX_CHASE_SPEED);
	}
	
	/**
	 * Check neighbourhood for criminals
	 * @param neighbours - the neighbours to check
	 * @return AgentFootprint of a detected criminal or none
	 */
	private AgentFootprint checkNeighbours(AgentFootprint[] neighbours) {
		for(AgentFootprint neighbour : neighbours) {
			if(neighbour.getRole() == AgentRole.Criminal) {
				return neighbour;
			}
		}
		return null;
	}
	
	/**
	 * Arrest message for a criminal
	 * @param time - the time
	 * @param criminal - the criminal to arrest
	 * @return the arrest message
	 */
	private Message arrestCriminal(int time, AgentFootprint criminal) {
		if(criminal == null) {
			return null;
		}
		
		ArrestCall call = new ArrestCall(time, this.getFootprint().getId(), criminal.getId());
		if(Math.abs(this.getFootprint().getPosition().getDistance(criminal.getPosition()) - call.getMaximumDistance()) > 1.0) {
			return null;
		}
		
		return call;
	}

	/**
	 * Getter
	 * @return false -> officers never quite the job
	 */
	@Override
	public boolean isDone() {
		return false;
	}

}
