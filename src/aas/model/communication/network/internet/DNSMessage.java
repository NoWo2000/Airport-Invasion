/**
 * 
 */
package aas.model.communication.network.internet;

import aas.model.communication.network.NetworkMessage;

/**
 * Message to register at the DNS under multiple names. Each ip message includes a host name
 * the dns forwards the message to all agents which are registered under this host name.
 * 
 * @author schier
 *
 */
public class DNSMessage extends NetworkMessage {
	
	private String[] hostNames;

	/**
	 * Constructor
	 * @param time - the time
	 * @param sender - the sender id
	 * @param receiver - the receiver id
	 * @param hostNames - the names under which the sender should be registered
	 */
	public DNSMessage(long time, Integer sender, int receiver, String[] hostNames) {
		super(time, sender, receiver);
		this.hostNames = hostNames;
	}

	/**
	 * Getter
	 * @return all host names
	 */
	public String[] getHostNames() {
		return this.hostNames;
	}

}
