/**
 * 
 */
package aas.model.communication.network.radio;

import java.util.Vector;

import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.communication.Broadcast;
import aas.model.communication.Message;
import aas.model.communication.network.NetworkMessage;

/**
 * @author schier
 *
 */
public class PoliceRadio extends NetworkMessage implements Broadcast {
	private static final int DEFAULT_RECEIVER = -1;
	
	private PoliceCode code;
	private AgentFootprint criminal;

	/**
	 * Constructor
	 * @param time - the time of the message
	 * @param sender - the sender
	 * @param code - the police code
	 * Remark: This message is broadcasted to all security agents
	 */
	public PoliceRadio(long time, int sender, PoliceCode code) {
		super(time, sender, DEFAULT_RECEIVER);
		this.code = code;
	}
	
	/**
	 * Constructor
	 * @param time - the time of the message
	 * @param sender - the sender
	 * @param code - the police code
	 * @param criminal - the footprint of the criminal which is sibject of the message (e.g. chase call)
	 * Remark: This message is broadcasted to all security agents
	 */
	public PoliceRadio(long time, int sender, PoliceCode code, AgentFootprint criminal) {
		this(time, sender, code);
		this.criminal = criminal;
	}
	
	/**
	 * Constructor
	 * @param time - the time of the message
	 * @param sender - the sender
	 * @param receiver - the receiver if the message (the message is only forwarded to this receiver, no broadcast)
	 * @param code - the police code
	 * @param criminal - the footprint of the criminal which is sibject of the message (e.g. chase call)
	 */
	private PoliceRadio(long time, int sender, int receiver, PoliceCode code, AgentFootprint criminal) {
		super(time, sender, receiver);
		this.code = code;
		this.criminal = criminal;
	}

	
	/**
	 * Getter
	 * @return the footprint of the criminal who is subject to the message
	 */
	public AgentFootprint getCriminal() {
		return criminal;
	}


	/**
	 * Getter
	 * @return the code
	 */
	public PoliceCode getCode() {
		return code;
	}

	/**
	 * Broadcasts the message
	 */
	@Override
	public Message[] broadcast(final AgentFootprint[] receivers) {		
		Vector<Message> messages = new Vector<>();
		
		
		for(AgentFootprint receiver : receivers) {
			if(receiver.getRole() == AgentRole.Security) {
				messages.add(new PoliceRadio(this.getTime(), this.getSender(), receiver.getId(), this.code, this.criminal));
			}
		}
		return messages.toArray(new Message[messages.size()]);
	}
	
}
