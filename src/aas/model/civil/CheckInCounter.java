/**
 * 
 */
package aas.model.civil;

import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import aas.controller.AgentController;
import aas.controller.logger.LoggerType;
import aas.model.Agent;
import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.StaticObject;
import aas.model.communication.Message;
import aas.model.communication.network.internet.DNSMessage;
import aas.model.communication.network.internet.IPMessage;
import aas.model.communication.network.internet.InternetPort;
import aas.model.communication.voice.Ticket;
import aas.model.communication.voice.TicketRequest;
import aas.model.util.Point;

/**
 * A static object that provides tickets and the flight location to the passangers
 * The flight information is received via internet where the checkin registers to the "airport" net.
 * 
 * @author schier
 *
 */
public class CheckInCounter implements Agent, StaticObject, InternetPort {
	
	private static final Logger EVENT_LOGGER = Logger.getLogger(LoggerType.EVENT + ".checkin");
	private static final Logger LOGGER = Logger.getLogger(AgentController.class.getName());
	
	private Integer gateway;
	private boolean dnsRegistered = false;
	
	private HashMap<String, Integer> ticketList = new HashMap<>();
	private HashMap<String, Point> gateList = new HashMap<>();
	
	private Vector<TicketRequest> queue = new Vector<>();
	
	private AgentFootprint footprint;
	
	/**
	 * Constructor
	 * @param id - agent id
	 * @param name - the name of the agent
	 * @param position - the position of the checkin desk
	 */
	public CheckInCounter(int id, String name, Point position) {
		this.footprint = new AgentFootprint(id, AgentRole.Civil, "checkin", name, position);
	}
	
	/**
	 * Getter
	 * @return the footprint of the agent
	 */
	@Override
	public AgentFootprint getFootprint() {
		return this.footprint;
	}

	/**
	 * Simulates the agent behaviour by checking IPMessages and TicketRequests
	 */
	@Override
	public Message[] simulate(int time, Message[] messages, AgentFootprint[] neighbours) {
		
		//1. Check incoming messages
		for(Message message : messages) {

			if(message instanceof IPMessage) {
				this.checkInternet(time, (IPMessage) message);
			}
			
			if(message instanceof TicketRequest) {
				TicketRequest request = (TicketRequest) message;
				if(this.isTicketRequestValid(request)) {
					this.queue.add(request);
				}
			}
			
		}
		
		//2. Send messages (register at DNS or send out a ticket)
		if((!dnsRegistered)&&(this.gateway != null)) {
			this.dnsRegistered = true;
			return new Message[] {new DNSMessage(time, this.getFootprint().getId(), this.gateway, new String[] {"airport"})};
		}
		
		if(this.queue.size() > 0) {
			Ticket ticket = this.generateTicket(time);
			if(ticket != null) {
				return new Message[] {ticket};
			}
		}
		
		return new Message[] {};
	}
	
	private void registerAtDns() {
		
		
	}

	/**
	 * Checks the IP MEssages for a inblock message of a flight
	 * @param time - the time of the message
	 * @param message - the message to check
	 */
	private void checkInternet(long time, IPMessage message) {
		if(! message.isMessage("inblock", new String[] {"flight", "seats","gate"})) {
			LOGGER.warning("In block message from " + message.getSender() + " can not be processed due to missing data.");
		}
		
		this.ticketList.put(message.getData("flight"), Integer.valueOf(message.getData("seats")));
		this.gateList.put(message.getData("flight"), Point.valueOf(message.getData("gate")));
		EVENT_LOGGER.info(time + " - flight " + message.getData("flight") + " registered at checkin " + this.getFootprint().getName());
	}
	
	/**
	 * Check
	 * @param request - the request to check
	 * @return true if request contains a flight which is stored with gate and seat position
	 */
	private boolean isTicketRequestValid(TicketRequest request) {
		if(!this.gateList.containsKey(request.getFlight()) || !this.ticketList.containsKey(request.getFlight())) {
			LOGGER.warning("flight " + request.getFlight() + " not listed in checkin");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Generates a ticket if the requested flight is registered at the desk
	 * @param time - the current time
	 * @param request - the request for the ticket
	 * @return the ticket
	 */
	private Ticket generateTicket(int time) {
		//1. Get next request
		if(this.queue.size() < 1) {
			return null;
		}
		
		TicketRequest request = this.queue.get(0);
		if(!this.isTicketRequestValid(request)) {
			LOGGER.warning("No ticket left for passenger " + request.getSender() + " waiting for flight " + request.getFlight());
			return null;
		}
		
		//2. Generate ticket
		final Point gate = this.gateList.get(request.getFlight());
		final Integer ticketNumber = this.ticketList.get(request.getFlight()) - 1;
		
		if(ticketNumber < 1) {
			this.ticketList.remove(request.getFlight());
			this.gateList.remove(request.getFlight());
		} else {
			this.ticketList.put(request.getFlight(), ticketNumber);
		}
		
		final Ticket ticket = new Ticket(time
				, this.footprint.getId()
				, request.getSender()
				, request.getFlight()
				, gate);
		
		EVENT_LOGGER.info(time + " - " + this.footprint.toString() + ": Ticket for passenger " + ticket.getReceiver() + " generated.");
		
		//3. Remove request from queue
		this.queue.remove(0);
		
		return ticket;
	}

	/**
	 * Checkin is never done
	 *
	 */
	//TODO Include opening hours?
	@Override
	public boolean isDone() {
		return false;
	}

	/**
	 * Width of the checkin desk
	 */
	@Override
	public double getWidth() {
		return 5.0;
	}

	/**
	 * Length if the checkin desk
	 */
	@Override
	public double getLength() {
		return 10.0;
	}
	
	/**
	 * Getter
	 * @return the number of flights registered
	 */
	public int getRegisteredFlights() {
		return this.gateList.keySet().size();
	}

	/**
	 * Setter
	 * @param dnsId - the id of the dns to forward messages
	 */
	@Override
	public void setGateway(int dnsId) {
		this.gateway = dnsId;
	}

}
