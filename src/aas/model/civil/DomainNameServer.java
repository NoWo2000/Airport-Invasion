/**
 * 
 */
package aas.model.civil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import aas.controller.logger.LoggerType;
import aas.model.Agent;
import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.StaticObject;
import aas.model.communication.Message;
import aas.model.communication.network.internet.DNSMessage;
import aas.model.communication.network.internet.IPMessage;
import aas.model.communication.network.internet.InternetGateway;
import aas.model.util.Point;

/**
 * Registers agents under a host name. All IP messages send to DNS with are forwarded according to their host name.
 * Multiple agents can be registered under the same host name to simulate networks and multicasts.
 *  
 * @author schier
 *
 */
public class DomainNameServer implements InternetGateway, Agent, StaticObject {

	private static final Logger EVENT_LOGGER = Logger.getLogger(LoggerType.EVENT + ".dns");
	
	private AgentFootprint footprint;
	private HashMap<String, Vector<Integer>> resourceRecord = new HashMap<>();

	
	/**
	 * Constructor
	 * @param id - the dns id
	 * @param name - the dns name
	 * @param position - the position at the airport
	 */
	public DomainNameServer(int id, String name, Point position) {
		this.footprint = new AgentFootprint(id, AgentRole.Civil, "dns", name, position);
	}

	/**
	 * Getter
	 * @return th agents footprint
	 */
	@Override
	public AgentFootprint getFootprint() {
		return this.footprint;
	}

	/**
	 * Simulates the dns behaviour.
	 * Reaction to incoming messages:
	 *  - DNSMEssage: Register host with his names
	 *  - IPMessage: Forward to the hosts with the given name
	 */
	@Override
	public Message[] simulate(int time, Message[] messages, AgentFootprint[] neighbours) {
		Vector<Message> outgoingMessages = new Vector<>();
		for(Message message : messages) {
			if(message instanceof DNSMessage) {
				DNSMessage registerMessage = (DNSMessage) message;
				this.registerHost(message.getSender(), registerMessage.getHostNames());
				EVENT_LOGGER.info(time + " - " + this.getFootprint().toString() + " : Agent " + registerMessage.getSender() + " registered at DNS.");
			}
			if(message instanceof IPMessage) {
				outgoingMessages.addAll(Arrays.asList(this.forwardIPMessage((IPMessage) message)));
				EVENT_LOGGER.info(time + " - " + this.getFootprint().toString() + " : Message of agent " + message.getSender() + " forwarded.");
			}
		}
		
		return outgoingMessages.toArray(new Message[outgoingMessages.size()]);
	}
	
	/**
	 * Registers a host
	 * @param id - the host id
	 * @param names - the host names
	 */
	private void registerHost(int id, String[] names) {
		for(String name : names) {
			if(!this.resourceRecord.containsKey(name)) {
				this.resourceRecord.put(name, new Vector<>());
			}
			this.resourceRecord.get(name).add(id);
		}
	}
	
	/**
	 * Forwards the messages
	 * @param message - the message to forward
	 * @return all messages that are forwarded 
	 */
	private Message[] forwardIPMessage(IPMessage message) {
		String hostName = message.getReceiverHostName();
		
		if(!this.resourceRecord.containsKey(hostName)) {
			return new Message[] {};
		}
		
		Vector<IPMessage> messages = new Vector<>();
		for(Integer address : this.resourceRecord.get(hostName)) {
			messages.add(message.copy(this.getFootprint().getId(), address));
		}
		return messages.toArray(new Message[messages.size()]);
		
	}

	/**
	 * Getter
	 * @return is never true...DNS always runs
	 */
	@Override
	public boolean isDone() {
		return false;
	}

	/**
	 * Getter
	 * @return size of the DNS server
	 */
	@Override
	public double getWidth() {
		return 1.5;
	}

	/**
	 * Getter
	 * @return size of the dns server in meters
	 */
	@Override
	public double getLength() {
		return 1.5;
	}

	/**
	 * Getter
	 * @return the name of the DNS
	 */
	@Override
	public String getName() {
		return this.footprint.getName();
	}

	/**
	 * Getter
	 * @return the id of the dns
	 */
	@Override
	public int getId() {
		return this.footprint.getId();
	}

}
