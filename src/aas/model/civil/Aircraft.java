/**
 * 
 */
package aas.model.civil;

import java.util.Vector;
import java.util.logging.Logger;

import aas.controller.logger.LoggerType;
import aas.model.Agent;
import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.communication.Message;
import aas.model.communication.network.internet.IPMessage;
import aas.model.communication.network.internet.InternetPort;
import aas.model.communication.voice.BoardingCall;
import aas.model.communication.voice.BoardingRequest;
import aas.model.util.Point;

/**
 * The aircraft is currently modeled as an agent which keeps its position and allows the 
 * passengers with a valid ticket to board one by one 
 * @author schier
 *
 */
public class Aircraft implements Agent, InternetPort {
	
	private static final Logger LOGGER_DEBUG = Logger.getLogger(LoggerType.DEBUG + ".aircraft");
	private static final Logger LOGGER_EVENT = Logger.getLogger(LoggerType.EVENT + ".aircraft");
	
	
	private AgentFootprint footprint;
	private Integer gateway;
	private Vector<BoardingRequest> boardingQueue = new Vector<>();
	private Integer seats;
	private int paxCounter = 0;
	private boolean inBlock = false;
	
	

	/**
	 * Constructor
	 * @param id - the agent id
	 * @param gate - the agent where the aircraft is waiting for the passangers
	 * @param flightName - the name of the flight
	 * @param seats - the number of available seats
	 */
	public Aircraft(int id, Point gate, String flightName, int seats) {
		super();
		this.footprint = new AgentFootprint(id, AgentRole.Civil, "aircraft", flightName , gate);
		this.seats = seats;
	}
	
	/**
	 * Getter
	 * @return the aent's footprint
	 */
	@Override
	public AgentFootprint getFootprint() {
		return this.footprint;
	}

	/**
	 * Simulates the aircraft behaviour
	 */
	@Override
	public Message[] simulate(int time, Message[] messages, AgentFootprint[] neighbours) {
		for(Message message : messages) {
			if(message instanceof BoardingRequest) {
				boardingQueue.add((BoardingRequest) message);
			}
		}
		
		//1. If aircraft not published as inblock, send a message via internet to all airport facilities (e.g. checkin) 
		if(!inBlock) {
			if (this.gateway != null) {
				IPMessage message = new IPMessage(time, this.getFootprint().getId(), this.gateway, "airport", "InBlock");
				message.addData("flight", this.getFootprint().getName());
				message.addData("gate", this.footprint.getPosition().toString());
				message.addData("seats", this.seats.toString());
				inBlock = true;
				return new Message[] {message};			
			} else {
				LOGGER_DEBUG.warning("No gateway defined for aircraft " + this.getFootprint().getName() + ". Unable to send inblock message to airport.");
			}
		}
		
		//2. If passenger waiting, let board
		if(boardingQueue.size() > 0) {
			LOGGER_EVENT.info(time + " - " 
						+ this.footprint + ": "
						+ "Boarding call for agent " + this.boardingQueue.get(0).getSender() + " with seat " + paxCounter);
			BoardingCall call = new BoardingCall(time,this.footprint.getId(), this.boardingQueue.get(0).getSender(), paxCounter);
			boardingQueue.remove(0);
			paxCounter++;
			return new Message[] {call};
		}
		
		return new Message[] {};
	}

	@Override
	public boolean isDone() {
		return this.seats <= this.paxCounter ;
	}

	@Override
	public void setGateway(int dnsId) {
		this.gateway = dnsId;
	}

}
