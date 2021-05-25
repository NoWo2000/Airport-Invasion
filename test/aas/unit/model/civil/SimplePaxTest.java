package aas.unit.model.civil;

import org.junit.Test;

import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.civil.pax.SimplePax;
import aas.model.communication.Message;
import aas.model.communication.voice.BoardingCall;
import aas.model.communication.voice.BoardingRequest;
import aas.model.communication.voice.Ticket;
import aas.model.communication.voice.TicketRequest;
import aas.model.util.Point;

public class SimplePaxTest {
	
	

	@Test
	public void testSimplePax() {
		SimplePax pax = new SimplePax(0, "john", new Point(0.0,0.0), "DLH123");
		assert(pax != null);
		assert(pax.getFootprint().getId() == 0);
		assert(pax.getFootprint().getRole() == AgentRole.Civil);
		assert(pax.getFootprint().getType().compareTo("pax")==0);
		assert(pax.getFootprint().getName().compareTo("john")==0);
	}

	@Test
	public void testSimulate() {
		SimplePax pax = new SimplePax(0, "john", new Point(0.0,0.0), "DLH123");
		
		//1. pax should be searching for checkin initially
		Message[] messages1 = pax.simulate(0, new Message[] {}, new AgentFootprint[] {});
		assert(messages1.length == 0);
		assert(pax.getSpeed() > 0.0);
		
		//2. Find checkIn
		AgentFootprint checkin = new AgentFootprint(1, AgentRole.Civil, "checkin", "checkin1", new Point(0.0,0.0));
		Message[] messages2 = pax.simulate(1, new Message[] {}, new AgentFootprint[] {checkin});
		assert(messages2.length == 1);
		assert(messages2[0] instanceof TicketRequest);
		assert(messages2[0].getReceiver() == 1);
		
		//3. Get Ticket
		Ticket ticket = new Ticket(2, 1, 0, "DLH123", new Point(1.0,1.0));
		Message[] messages3 = pax.simulate(2, new Message[] {ticket}, new AgentFootprint[] {});
		assert(messages3.length == 0);
		
		//4. Walk to gate
		Message[] messages4 = pax.simulate(3, new Message[] {}, new AgentFootprint[] {});
		assert(messages4.length == 0);
		assert(pax.getSpeed() > 0.0);
		
		//5. Send message to aircraft
		AgentFootprint aircraft = new AgentFootprint(2, AgentRole.Civil, "aircraft", "DLH123", new Point(1.0,1.0));
		Message[] messages5 = pax.simulate(4, new Message[] {}, new AgentFootprint[] {aircraft});
		assert(messages5.length == 1);
		assert(messages5[0] instanceof BoardingRequest);
		assert(messages5[0].getReceiver() == 2);
		
		//3. Get Ticket
		BoardingCall call = new BoardingCall(5, 2, 0, 1);
		Message[] messages6 = pax.simulate(2, new Message[] {call}, new AgentFootprint[] {});
		assert(messages6.length == 0);
		assert(pax.isDone());
	}

	@Test
	public void testGetSpeed() {
		SimplePax pax = new SimplePax(0, "john", new Point(0.0,0.0), "DLH123");
		pax.simulate(0, new Message[] {}, new AgentFootprint[] {});
		assert(pax.getSpeed() > 0.0);
	}

	@Test
	public void testGetDirection() {
		Point start = new Point(0.0,0.0);
		SimplePax pax = new SimplePax(0, "john", start, "DLH123");
		pax.simulate(0, new Message[] {}, new AgentFootprint[] {});
		double refDirection = start.getDirection(pax.getFootprint().getPosition());
		assert(!pax.getFootprint().getPosition().equals(new Point(0.0,0.0)));
		assert(Math.abs(pax.getDirection() - refDirection) < 1.0);
	}

	@Test
	public void testGetFootprint() {
		SimplePax pax = new SimplePax(0, "john", new Point(0.0,0.0), "DLH123");
		assert(pax.getFootprint() != null);
	}

	@Test
	public void testIsDone() {
		SimplePax pax = new SimplePax(0, "john", new Point(0.0,0.0), "DLH123");
		pax.simulate(0, new Message[] {}, new AgentFootprint[] {});
		
	}

}
