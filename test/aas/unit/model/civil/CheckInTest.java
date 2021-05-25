package aas.unit.model.civil;

import org.junit.Test;

import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.civil.CheckInCounter;
import aas.model.communication.Message;
import aas.model.communication.network.internet.DNSMessage;
import aas.model.communication.network.internet.IPMessage;
import aas.model.communication.voice.Ticket;
import aas.model.communication.voice.TicketRequest;
import aas.model.util.Point;

public class CheckInTest {

	@Test
	public void testCheckInCounter() {
		CheckInCounter checkin = new CheckInCounter(0,"checkin1", new Point(0.0,0.0));
		assert(checkin != null);
	}

	@Test
	public void testGetFootprint() {
		CheckInCounter checkin = new CheckInCounter(0,"checkin1", new Point(0.0,0.0));
		assert(checkin.getFootprint().getId() == 0);
		assert(checkin.getFootprint().getRole() == AgentRole.Civil);
		assert(checkin.getFootprint().getType().compareTo("checkin") == 0);
		assert(checkin.getFootprint().getName().compareTo("checkin1") == 0);
	}

	@Test
	public void testSimulate() {
		CheckInCounter checkin = new CheckInCounter(0,"checkin1", new Point(0.0,0.0));
		
		//1. Check checkin does nothing upon empty input
		Message[] messages0 = checkin.simulate(0, new Message[] {}, new AgentFootprint[] {});
		assert(messages0.length == 0);
		
		//2. Check checkin registeres flight
		IPMessage inBlockMessage = new IPMessage(1, 1, 0, "checkin", "inblock");
		inBlockMessage.addData("flight", "DLH123");
		inBlockMessage.addData("gate", new Point(0.0,0.0).toString());
		inBlockMessage.addData("seats", "2");
		Message[] messages1 = checkin.simulate(1, new Message[] {inBlockMessage}, new AgentFootprint[] {});
		assert(messages1.length == 0);
		assert(checkin.getRegisteredFlights() == 1);
		
		//3. Check checkin answers with one ticket upon two requests
		TicketRequest request1 = new TicketRequest(2, 2, 0, "DLH123");
		TicketRequest request2 = new TicketRequest(2, 3, 0, "DLH123");
		Message[] messages2 = checkin.simulate(2, new Message[] {request1, request2}, new AgentFootprint[] {});
		assert(messages2.length == 1);
		assert(messages2[0] instanceof Ticket);
		assert(messages2[0].getReceiver() == 2);
		
		//4. Check checki answers with one ticket if no further requests
		Message[] messages3 = checkin.simulate(3, new Message[] {}, new AgentFootprint[] {});
		assert(messages3.length == 1);
		assert(messages3[0] instanceof Ticket);
		assert(messages3[0].getReceiver() == 3);
		
	}
	
	@Test
	public void testSimulateWithGateway() {
		CheckInCounter checkin = new CheckInCounter(0,"checkin1", new Point(0.0,0.0));
		checkin.setGateway(1);
		
		//1. Check checkin does nothing upon empty input
		Message[] messages0 = checkin.simulate(0, new Message[] {}, new AgentFootprint[] {});
		assert(messages0.length == 1);
		assert(messages0[0] instanceof DNSMessage);
		assert(messages0[0].getReceiver() == 1);		
	}

	@Test
	public void testIsDone() {
		CheckInCounter checkin = new CheckInCounter(0,"checkin1", new Point(0.0,0.0));
		assert(!checkin.isDone());
	}

	@Test
	public void testGetWidth() {
		CheckInCounter checkin = new CheckInCounter(0,"checkin1", new Point(0.0,0.0));
		assert(checkin.getWidth() > 0.0);
	}

	@Test
	public void testGetLength() {
		CheckInCounter checkin = new CheckInCounter(0,"checkin1", new Point(0.0,0.0));
		assert(checkin.getLength() > 0.0);
	}

}
