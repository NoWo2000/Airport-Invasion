/**
 * 
 */
package aas.model.civil.pax;

import java.util.Random;

import aas.model.AgentFootprint;
import aas.model.communication.Message;
import aas.model.communication.voice.TicketRequest;
import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class SearchCheckIn implements State {
	
	private static double SPEED = 3.0;
	private Point position;
	private AgentFootprint checkIn;
	private String flight;
	
	
	

	/**
	 * @param position
	 */
	public SearchCheckIn(Point position, String flight) {
		super();
		this.position = position;
		this.flight = flight;
	}

	@Override
	public Point calculateNextStep(int time, Message[] requests, AgentFootprint[] neighbours) {
		checkIn = this.lookForCheckin(neighbours);
		if(checkIn == null) {
			this.position = move(this.position);
		};
		
		return this.position;
	}

	private Point move(Point oldPosition) {
		Random random = new Random();
		double direction = Integer.valueOf(random.nextInt(360)).doubleValue();
		double dx = SPEED * Math.cos(direction);
		double dy = SPEED * Math.sin(direction);
		Point newPosition = new Point(oldPosition.getX(), oldPosition.getY());
		newPosition.translate((int) Math.round(dx), (int) Math.round(dy));
		return newPosition;
	}
	
	private AgentFootprint lookForCheckin(AgentFootprint[] neighbours) {
		for(AgentFootprint neighbour : neighbours) {
			if(neighbour.getType().compareTo("checkin")==0) {
				return neighbour;
			}
		}
		return null;
	}

	

	@Override
	public Message[] getRequests(int time, AgentFootprint me) {
		if(checkIn == null) {
			return new Message[] {};
		}
		TicketRequest request = new TicketRequest(time, me.getId(), checkIn.getId(), this.flight);
		return new Message[] {request};
	}

	@Override
	public State getNextState() {
		if(checkIn == null) {
			return null;
		}
		return new WaitForTicket(this.position);
	}
	
	/**
	 * Search for check in and request ticket
	 */
	/*public void checkNeighbours(Agent[] neighbours) {
		for(Agent neighbour : neighbours) {
			if(neighbour.getRole() == AgentRole.Object) 
				&& (neighbour.getType().compareTo("checkin"))) {
				neighbour.sendRequest("ticket");
			}
		}
	}*/



}
