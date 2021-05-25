/**
 * 
 */
package aas.model.communication;

import aas.model.AgentFootprint;

/**
 * @author schier
 *
 */
public interface Broadcast extends Message {
	
	Message[] broadcast(AgentFootprint receivers[]);

}
