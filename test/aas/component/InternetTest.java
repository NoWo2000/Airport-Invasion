package aas.component;

import org.junit.Test;

import aas.model.AgentFootprint;
import aas.model.civil.Aircraft;
import aas.model.civil.CheckInCounter;
import aas.model.civil.DomainNameServer;
import aas.model.communication.Message;
import aas.model.communication.network.internet.DNSMessage;
import aas.model.communication.voice.Ticket;
import aas.model.communication.voice.TicketRequest;
import aas.model.util.Point;

public class InternetTest {

	@Test
	public void test() {
		Aircraft aircraft = new Aircraft(0,new Point(0.0,0.0),"DLH123",50);
		CheckInCounter checkIn = new CheckInCounter(1, "checkin1", new Point(10.0,10.0));
		DomainNameServer dns = new DomainNameServer(2,"server1", new Point(0.0, 0.0));
		
		DNSMessage registerCheckIn = new DNSMessage(0,1,2,new String[] {"airport"});
		dns.simulate(0, new Message[] {registerCheckIn}, new AgentFootprint[] {});
		
		aircraft.setGateway(2);
		Message[] aircraftMessages = aircraft.simulate(1, new Message[] {}, new AgentFootprint[] {});
		assert(aircraftMessages.length == 1);
		Message[] dnsMessages = dns.simulate(1, aircraftMessages, new AgentFootprint[] {});
		assert(dnsMessages.length == 1);
		checkIn.simulate(1, dnsMessages, new AgentFootprint[] {});
		Message ticketRequest = new TicketRequest(0,3,1,"DLH123");
		Message[] checkInMessages = checkIn.simulate(2, new Message[] {ticketRequest}, new AgentFootprint[] {});
		assert(checkInMessages.length == 1);
		assert(checkInMessages[0] instanceof Ticket);
	}

}
