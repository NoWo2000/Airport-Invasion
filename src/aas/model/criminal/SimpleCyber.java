/**
 * 
 */
package aas.model.criminal;

import java.util.Arrays;
import java.util.Vector;

import aas.model.Agent;
import aas.model.AgentFootprint;
import aas.model.Moveable;
import aas.model.communication.Message;
import aas.model.communication.network.internet.IPMessage;
import aas.model.util.Point;

/**
 * The Simple Cyber agent inserts a non existing flight into the checkin desk
 * and requests a ticket for this flight.
 * @author schier
 *
 */
public class SimpleCyber implements Agent, Moveable {
	
	Braggart braggert;
	
	
	/**
	 * Constructor
	 * @param id - the braggart id
	 * @param name - the name of the braggart
	 * @param start - the starting point
	 */
	public SimpleCyber(int id, String name, Point start) {
		super();
		this.braggert = new SimpleBraggart(id, name, start);
	}

	/**
	 * Getter
	 * @return speed in meters per sim step
	 */
	@Override
	public double getSpeed() {
		return braggert.getSpeed();
	}

	/**
	 * Getter
	 * @return The Direction in deg
	 */
	@Override
	public double getDirection() {
		return braggert.getDirection();
	}

	/**
	 * Getter
	 * @return the agent's footprint
	 */
	@Override
	public AgentFootprint getFootprint() {
		return braggert.getFootprint();
	}

	/**
	 * Simulates the cyber agent behaviour by chckin for a checkin in the vicinity. If found a flight is published
	 */
	@Override
	public Message[] simulate(int time, Message[] messages, AgentFootprint[] neighbours) {
		Vector<Message> out = new Vector<>();
		for(AgentFootprint neighbour : neighbours) {
			if(neighbour.getType().compareTo("checkin") == 0) {
				String flight = braggert.getFlightToBoard();
				IPMessage message = new IPMessage(time, this.getFootprint().getId(), neighbour.getId(), "checkin", "inblock");
				message.addData("flight", flight);
				message.addData("gate", new Point(0.0,0.0).toString());
				message.addData("seats", "1");
				braggert.setFlightToBoard(flight);
			}
		}
		
		out.addAll(Arrays.asList(braggert.simulate(time, messages, neighbours)));
		
		return out.toArray(new Message[out.size()]);
	}

	/**
	 * Getter
	 * @return if agent is done
	 */
	@Override
	public boolean isDone() {
		return braggert.isDone();
	}

}
