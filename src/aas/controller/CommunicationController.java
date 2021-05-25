/**
 * 
 */
package aas.controller;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import aas.AirportAgentSimulation;
import aas.model.AgentFootprint;
import aas.model.communication.Broadcast;
import aas.model.communication.Message;

/**
 * @author schier
 *
 */
public class CommunicationController {
	
	private static final Logger LOGGER = Logger.getLogger(AirportAgentSimulation.class.getName());
	
	
	private Vector<Message> incoming = new Vector<>();
	private Vector<Message> outgoing = new Vector<>();
	
	private AgentController agentController;

	/**
	 * 
	 */
	public CommunicationController(AgentController controller) {
		this.agentController = controller;
	}
	
	public void addMessages(final Message[] messages) {
		for(Message message : messages) {	
			if(message == null) {
				continue;
			}
			if(message instanceof Broadcast) {
				this.addBroadcast((Broadcast) message);
			} else {
				this.addSingleMessage(message);
			}
		}
	}
	
	private void addSingleMessage(Message message) {
		if(message == null) {
			throw new NullPointerException();
		}
		
		if(message.getReceiver() == null) {
			throw new IllegalArgumentException("Receiver of a single message must not be null: " + message.toString());
		}
		
		AgentFootprint sender = agentController.getAgent(message.getSender());
		AgentFootprint receiver = agentController.getAgent(message.getReceiver());
		if((sender == null) || (receiver == null)) {
			LOGGER.log(Level.WARNING, "unkown sender or receiver");
			return;
		}
		
		if(sender.getPosition().getDistance(receiver.getPosition()) > message.getMaximumDistance()) {
			return;
		}
		
		
		this.incoming.add(message);
	}
	
	private void addBroadcast(Broadcast broadcast) {
		AgentFootprint sender = agentController.getAgent(broadcast.getSender());
		AgentFootprint[] receivers = agentController.getFootprints(sender.getPosition(), broadcast.getMaximumDistance());
		for(Message message : broadcast.broadcast(receivers)) {
			if(message != null) {
				this.addSingleMessage(message);
			}
		}
	}
	
	public void transfer(int time) {
		outgoing.clear();
		outgoing.addAll(incoming);
		incoming.clear();
	}
	
	public Message[] getOutgoing() {
		return this.outgoing.toArray(new Message[this.outgoing.size()]);
	}
	
	
	public Message[] getOutgoing(int agentId) {
		Vector<Message> messages = new Vector<>();
		for(Message message : this.outgoing) {
		if(message.getReceiver() - agentId == 0) {
			messages.add(message);
		}
	}
		
	return messages.toArray(new Message[messages.size()]);
	}

}
