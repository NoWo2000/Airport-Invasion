/**
 * 
 */
package aas.model.communication.voice;

/**
 * Boarding Call allows a passanger to come aborad
 * @author schier
 *
 */
public class BoardingCall extends VoiceMessage {
	
	private Integer seat;

	/**
	 * Constructor
	 * @param time - the time of the boarding call
	 * @param sender - the sender of the boarding call
	 * @param receiver - the receiver of the boarding call
	 * @param seatNumber - the assigned seat to the passanger
	 */
	public BoardingCall(long time, Integer sender, Integer receiver, int seatNumber) {
		super(time, sender, receiver);
		this.setSeat(seatNumber);
	}

	/**
	 * Getter
	 * @return the seat
	 */
	public Integer getSeat() {
		return seat;
	}

	/**
	 * Setter
	 * @param seatNumber - the seat Number
	 */
	private void setSeat(int seatNumber) {
		this.seat = seatNumber;
	}

	
	

}
