/**
 * 
 */
package aas.model.criminal;

import java.util.Random;

import aas.model.Agent;
import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.Moveable;
import aas.model.communication.Message;
import aas.model.communication.manual.ArrestCall;
import aas.model.communication.voice.Ticket;
import aas.model.communication.voice.TicketRequest;
import aas.model.util.Point;

/**
 * The braggart (germ. "Aufschneider/Angeber") walks around and as soon as he / she detects a checkin, 
 * he tries to get a ticket by guessing the name of a lufthansa ("DLH") flight.
 * 
 * @author schier
 *
 */
public class SimpleBraggart implements Agent, Moveable, Braggart {
	
	private static final Double SPEED = 4.0;
	
	private AgentFootprint footprint;
	private Point currentPosition;
	private Point lastPosition;
	private AgentFootprint checkIn;
	private Ticket ticket;
	private String flight;
	private boolean arrested;
	
		

	/**
	 * Constructor
	 * @param id - the braggart id
	 * @param name - the name of the braggart
	 * @param start - the starting point
	 */
	public SimpleBraggart(int id, String name, Point start) {
		super();
		this.footprint = new AgentFootprint(id, AgentRole.Criminal, "braggart", name, start);
		this.currentPosition = start;
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
	 * Simulates the braggarts behaviour
	 */
	@Override
	public Message[] simulate(int time, Message[] messages, AgentFootprint[] neighbours) {
		this.lastPosition = this.currentPosition;
		
		this.checkMessages(messages);
		this.checkNeighbours(neighbours);
		
		if(this.checkIn != null) {
			TicketRequest request = new TicketRequest(time, footprint.getId(), checkIn.getId(), this.getFlightToBoard());
			return new Message[] {request};
		} else {
			walkAround();
		}
			
		return new Message[] {};
	}
	
	/**
	 * Checks the messages
	 * @param messages - the incoming messages
	 */
	//TODO arrest should not be done by the agent himself
	private void checkMessages(Message[] messages) {
		for(Message message : messages) {
			if(message instanceof Ticket) {
				this.ticket = (Ticket) message;
			}
		
			if(message instanceof ArrestCall) {
				arrested = true;
			}
		}
	}
	
	/**
	 * Random walk around
	 */
	private void walkAround() {
		this.lastPosition = this.currentPosition;
		Random random = new Random();
		double direction = Integer.valueOf(random.nextInt(360)).doubleValue();
		double dx = SPEED * Math.cos(direction);
		double dy = SPEED * Math.sin(direction);
		Point newPosition = new Point(this.currentPosition.getX(), this.currentPosition.getY());
		newPosition.translate((int) Math.round(dx), (int) Math.round(dy));
		this.currentPosition = newPosition;
	}
	
	/**
	 * Checks the neighbours
	 * @param neighbours - the neighbours for checkin
	 */
	private void checkNeighbours(AgentFootprint[] neighbours) {
		for(AgentFootprint neighbour : neighbours) {
			if(neighbour.getType().compareTo("checkin") == 0) {
				this.checkIn = neighbour;
			}
		}
	}
	 
	/**
	 * Getter
	 * @return if ticket received or arrested
	 */
	@Override
	public boolean isDone() {
		return (ticket != null) || arrested;
	}

	/**
	 * Getter
	 * @return speed in meters per simulation step
	 */
	@Override
	public double getSpeed() {
		if(lastPosition == null) {
			return 0.0;
		}
		return this.currentPosition.getDistance(this.lastPosition);
	}

	/**
	 * Getter
	 * @return direction in deg
	 */
	@Override
	public double getDirection() {
		if(lastPosition == null) {
			return 0.0;
		}
		return this.currentPosition.getDirection(this.lastPosition);
	}

	/**
	 * Getter
	 * @return the flight the raggert wants to take (either random or speficily set)
	 */
	@Override
	public String getFlightToBoard() {
		if(this.flight == null) {
			Random random = new Random();
			return "DLH" + random.nextInt(999);
		}
		
		return this.flight;
	}

	/**
	 * Setter
	 * @param flight - the flight the braggert should take
	 */
	@Override
	public void setFlightToBoard(String flight) {
		this.flight = flight;
	}
	
	

}
