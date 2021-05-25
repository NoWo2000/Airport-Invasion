/**
 * 
 */
package aas.model.civil.pax;

import aas.model.AgentFootprint;
import aas.model.communication.Message;
import aas.model.communication.voice.BoardingRequest;
import aas.model.communication.voice.Ticket;
import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class GoToGate implements State {
	
	private static double MAX_SPEED = 5.0;
	private Point position = new Point();
	private Ticket ticket;
	private AgentFootprint aircraft;
	
	public GoToGate(Point start, Ticket ticket) {
		this.ticket = ticket;
		this.position = start;
	}


	@Override
	public Point calculateNextStep(int time, Message[] requests, AgentFootprint[] neighbours) {
		aircraft = this.lookForAircraft(neighbours);
		this.position = position.moveTo(position, ticket.getGate(), MAX_SPEED);
		return position;
	}
	
	private AgentFootprint lookForAircraft(AgentFootprint[] neighbours) {
		for(AgentFootprint neighbour : neighbours) {
			if((neighbour.getType().compareTo("aircraft")==0) 
					&& (neighbour.getName().compareTo(ticket.getFlight()) == 0)) {
				return neighbour;
			}
		}
		return null;
	}


	@Override
	public Message[] getRequests(int time, AgentFootprint me) {
		if(aircraft == null) {
			return new Message[] {};
		}
		BoardingRequest request = new BoardingRequest(time, me.getId(), aircraft.getId());
		return new Message[] {request};
	}


	@Override
	public State getNextState() {
		if(this.aircraft == null) {
			return null;
		}
		return new WaitForBoardingCall(this.position);
	}

}
