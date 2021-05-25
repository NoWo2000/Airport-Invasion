/**
 * 
 */
package aas.model.civil.pax;

import aas.model.AgentFootprint;
import aas.model.communication.Message;
import aas.model.communication.voice.Ticket;
import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class WaitForTicket implements State {
	
	private Point position = new Point(); 
	private State nextState = null;
	

	/**
	 * @param position
	 */
	public WaitForTicket(Point position) {
		super();
		this.position = position;
	}

	@Override
	public Point calculateNextStep(int time, Message[] requests, AgentFootprint[] neighbours) {
		for(Message request : requests) {
			checkMessage(request);
		}
		return position;
	}
	
	private void checkMessage(Message request) {
		if(request instanceof Ticket) {
			this.nextState = new GoToGate(this.position, (Ticket) request);
		}
	}

	@Override
	public Message[] getRequests(int time, AgentFootprint me) {
		return new Message[] {};
	}

	@Override
	public State getNextState() {
		return this.nextState;
	}
	
	
}
