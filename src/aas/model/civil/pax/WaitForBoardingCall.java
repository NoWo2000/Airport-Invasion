/**
 * 
 */
package aas.model.civil.pax;

import aas.model.AgentFootprint;
import aas.model.communication.Message;
import aas.model.communication.voice.BoardingCall;
import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class WaitForBoardingCall implements State {
	
	private Point position = new Point();
	private State nextState = null;
	

	/**
	 * @param position
	 */
	public WaitForBoardingCall(Point position) {
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
		if(request instanceof BoardingCall) {
			this.nextState = new Boarded(this.position);
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
