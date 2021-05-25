/**
 * 
 */
package aas.model.communication.voice;

import aas.model.util.Point;

/**
 * Ticket that leads the way to the plane
 * @author schier
 *
 */
public class Ticket extends VoiceMessage {
	
	private String flight;
	private Point gate;

	/**
	 * Constructor
	 * @param time - the time of the ticket generation
	 * @param sender - the sender of the ticket
	 * @param receiver - the receiver of the ticket
	 * @param flight - the flight's name
	 * @param gate - the flight's gate  
	 */
	public Ticket(long time, Integer sender, Integer receiver, String flight, Point gate) {
		super(time, sender, receiver);
		this.flight = flight;
		this.gate = gate;
	}

	/**
	 * Getter
	 * @return the flight's name
	 */
	public String getFlight() {
		return flight;
	}

	/**
	 * Getter
	 * @return the flight's gate
	 */
	public Point getGate() {
		return gate;
	}
	
	

}
