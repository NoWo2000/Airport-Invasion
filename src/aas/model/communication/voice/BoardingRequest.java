/**
 * 
 */
package aas.model.communication.voice;

/**
 * Request to come aboard
 * @author schier
 *
 */
public class BoardingRequest extends VoiceMessage {

	/**
	 * Constructor
	 * @param time - the time of the request
	 * @param sender - the sender of the request
	 * @param receiver - the receiver of the request
	 */
	public BoardingRequest(long time, Integer sender, Integer receiver) {
		super(time, sender, receiver);
	}

}
