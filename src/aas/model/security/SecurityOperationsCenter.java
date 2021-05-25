/**
 * 
 */
package aas.model.security;

import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Logger;

import aas.controller.logger.LoggerType;
import aas.model.Agent;
import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.communication.Message;
import aas.model.communication.network.radio.PoliceCode;
import aas.model.communication.network.radio.PoliceRadio;
import aas.model.util.Point;

/**
 * Security Operations Center (SOC) monitors all officers. As soon as a criminal is detected and reported,
 * a chase call to all officers is announced as soon as the criminal is reported to be arrested.
 * 
 * @author schier
 *
 */
public class SecurityOperationsCenter implements Agent {
	
	private static final Logger EVENT_LOGGER = Logger.getLogger(LoggerType.EVENT + ".soc");
	
	private AgentFootprint footprint;
	private Vector<Integer> officers = new Vector<>();

	/**
	 * Constructor
	 * @param id - the soc id
	 * @param name - the soc name
	 * @param position - the soc position
	 */
	public SecurityOperationsCenter(int id, String name, Point position) {
		this.footprint = new AgentFootprint(id, AgentRole.Security, "SecurityOperationsCenter", name, position);
	}

	/**
	 * Getter
	 * @return the footprint
	 */
	@Override
	public AgentFootprint getFootprint() {
		return this.footprint;
	}

	/**
	 * Simulates the SOC behaviour
	 * Incoming Detected Call -> Chase message
	 * Incoming Arrested Call -> Stop chase message
	 */
	@Override
	public Message[] simulate(int time, Message[] messages, AgentFootprint[] neighbours) {
		Vector<Message> outMessages = new Vector<>();
		for(Message message : messages) {
			if(message instanceof PoliceRadio) {
				outMessages.addAll(Arrays.asList(this.listenToRadio(time, (PoliceRadio) message)));
			}
		}
		
		return outMessages.toArray(new Message[outMessages.size()]);
	}
	
	/**
	 * Checks the messages
	 * @param time - the time
	 * @param radio - the incoming radio
	 * @return the messages to forward
	 */
	private Message[] listenToRadio(long time, PoliceRadio radio) {
		switch(radio.getCode()) {
			case OfficerOnDuty: this.officers.add(radio.getSender()); break;
			
			case OfficerOffDuty: this.officers.remove(radio.getSender()); break;
			
			case CriminalDetected: 
				EVENT_LOGGER.info(time + " - " + this.getFootprint().toString() + ": All officers chase criminal " + radio.getCriminal().getId());
				return this.sendStartChase(time, radio.getCriminal());
				
			case CriminalArrested: 
				EVENT_LOGGER.info(time + " - " + this.getFootprint().toString() + ": All officers stop chasing.");
				return this.sendStopChase(time, radio.getCriminal());
				
			default: return new Message[]{};
		}
		return new Message[]{};
	}
	
	/**
	 * Defined chase message
	 * @param time - the time
	 * @param criminal - the criminal
	 * @return the chase message
	 */
	private Message[] sendStartChase(long time, AgentFootprint criminal) {
		return new Message[] {new PoliceRadio(time, this.footprint.getId(), PoliceCode.ChaseCriminal, criminal)};
	}
	
	/**
	 * Define stop message
	 * @param time - the time
	 * @param criminal - the criminal
	 * @return the stop chase message
	 */
	private Message[] sendStopChase(long time, AgentFootprint criminal) {
		return new Message[] {new PoliceRadio(time, this.footprint.getId(), PoliceCode.StopChase, criminal)};
	}
	
	/**
	 * Getter
	 * @return the number of officers on duty
	 */
	public int getShiftSize() {
		return this.officers.size();
	}

	/**
	 * Getter
	 * @return false --> SOC never sleeps
	 */
	@Override
	public boolean isDone() {
		return false;
	}

}
