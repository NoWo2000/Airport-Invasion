/**
 * 
 */
package aas.model.communication.voice;

import aas.model.communication.AbstractMessage;

/**
 * @author schier
 *
 */
public class VoiceMessage extends AbstractMessage {
	
	private static final double MAXIMUM_DISTANCE = 13.0;
	//More realtistic:
	//private static final double MAXIMUM_DISTANCE = 150.0;

	/**
	 * Constructor
	 * @param time - the time of the voice message
	 * @param sender - the sender of the voice message
	 * @param receiver - the receiver of the voice message
	 */
	public VoiceMessage(long time, Integer sender, Integer receiver) {
		super(time, sender, receiver);
	}

	/**
	 * Getter
	 * @return the maximum distance in meters
	 */
	@Override
	public double getMaximumDistance() {
		return MAXIMUM_DISTANCE;
	}

}
