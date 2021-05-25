/**
 * 
 */
package aas.unit.model.communication;

import org.junit.Test;

import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.communication.Message;
import aas.model.communication.network.radio.PoliceCode;
import aas.model.communication.network.radio.PoliceRadio;
import aas.model.util.Point;

/**
 * @author schier
 *
 */
public class PoliceRadioTest {

	/**
	 * Test method for {@link aas.model.communication.network.radio.PoliceRadio#PoliceRadio(long, java.lang.Integer, java.lang.Integer, aas.model.communication.network.radio.PoliceCode)}.
	 */
	@Test
	public void testPoliceRadioLongIntegerIntegerPoliceCode() {
		PoliceRadio message = new PoliceRadio(0L,0,PoliceCode.ChaseCriminal);
		assert(message != null);
		assert(message.getTime() == 0L);
		assert(message.getSender() == 0);
		assert(message.getReceiver() == -1);
		assert(message.getCode() == PoliceCode.ChaseCriminal);
		assert(message.getCriminal() == null);
	}

	/**
	 * Test method for {@link aas.model.communication.network.radio.PoliceRadio#PoliceRadio(long, java.lang.Integer, java.lang.Integer, aas.model.communication.network.radio.PoliceCode, aas.model.AgentFootprint)}.
	 */
	@Test
	public void testPoliceRadioLongIntegerIntegerPoliceCodeAgentFootprint() {
		AgentFootprint criminal = new AgentFootprint(23, AgentRole.Criminal, "cheeky", "badJoe", new Point(0.0,0.0));
		PoliceRadio message = new PoliceRadio(0L,0,PoliceCode.ChaseCriminal, criminal);
		assert(message != null);
		assert(message.getTime() == 0L);
		assert(message.getSender() == 0);
		assert(message.getReceiver() == -1);
		assert(message.getCode() == PoliceCode.ChaseCriminal);
		assert(message.getCriminal().equals(criminal));
	}

	/**
	 * Test method for {@link aas.model.communication.network.radio.PoliceRadio#broadcast(aas.model.AgentFootprint[])}.
	 */
	@Test
	public void testBroadcast() {
		AgentFootprint officer1 = new AgentFootprint(1, AgentRole.Security, "simpleofficer", "johnMclain", new Point(0.0,0.0));
		AgentFootprint officer2 = new AgentFootprint(2, AgentRole.Security, "simpleofficer", "LuckyLuke", new Point(0.0,0.0));
		AgentFootprint criminal = new AgentFootprint(23, AgentRole.Criminal, "cheeky", "badJoe", new Point(0.0,0.0));
		PoliceRadio message = new PoliceRadio(0L,0,PoliceCode.ChaseCriminal, criminal);
		
		Message[] result = message.broadcast(new AgentFootprint[] {officer1,officer2,criminal});
		
		assert(result != null);
		assert(result.length == 2);
		assert(result[0].getReceiver() == officer1.getId());
		assert(result[1].getReceiver() == officer2.getId());
	}

}
