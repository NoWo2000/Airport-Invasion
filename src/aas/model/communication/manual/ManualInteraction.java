/**
 * 
 */
package aas.model.communication.manual;

import aas.model.communication.AbstractMessage;

/**
 * @author schier
 *
 */
public class ManualInteraction extends AbstractMessage {
	
	private static final double MAXIMUM_DISTANCE = 1.0;

	/**
	 * @param time
	 * @param sender
	 * @param receiver
	 */
	public ManualInteraction(long time, Integer sender, Integer receiver) {
		super(time, sender, receiver);
	}

	@Override
	public double getMaximumDistance() {
		return MAXIMUM_DISTANCE;
	}

}
