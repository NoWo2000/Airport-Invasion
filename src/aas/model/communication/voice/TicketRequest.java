/**
 * 
 */
package aas.model.communication.voice;

/**
 * Request that is provided e.g. to the checkin desk to get a ticket
 * @author schier
 *
 */
public class TicketRequest extends VoiceMessage {
	
	private String flight;

	/**
	 * Constructor
	 * @param time - the time of the request
	 * @param sender - the sender id
	 * @param receiver - the receiver id
	 * @param flight - the name of the flight for which the ticket is requested
	 */
	public TicketRequest(long time, Integer sender, Integer receiver, String flight) {
		super(time, sender, receiver);
		this.flight = flight;
	}

	/**
	 * Getter
	 * @return the flight name
	 */
	public String getFlight() {
		return flight;
	}

}
