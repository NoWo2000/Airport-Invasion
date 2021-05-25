/**
 * 
 */
package aas.model.civil.pax;

import aas.model.AgentFootprint;
import aas.model.communication.Message;
import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class Boarded implements State {
	
	private Point position = new Point();
	

	/**
	 * @param position
	 */
	public Boarded(Point position) {
		super();
		this.position = position;
	}

	@Override
	public Point calculateNextStep(int time, Message[] requests, AgentFootprint[] neighbours) {
		return position;
	}

	@Override
	public Message[] getRequests(int time, AgentFootprint me) {
		return new Message[] {};
	}

	@Override
	public State getNextState() {
		return null;
	}
	
	
}
