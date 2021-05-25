package aas.unit.model.civil;

import org.junit.Test;

import aas.model.AgentFootprint;
import aas.model.AgentRole;
import aas.model.civil.Aircraft;
import aas.model.communication.Message;
import aas.model.communication.voice.BoardingCall;
import aas.model.communication.voice.BoardingRequest;
import aas.model.util.Point;

public class AircraftTest {

	@Test
	public void testAircraft() {
		Aircraft aircraft = new Aircraft(0, new Point(0.0,0.0), "DLH123", 1);
		assert(aircraft != null);
	}

	@Test
	public void testGetFootprint() {
		Aircraft aircraft = new Aircraft(0, new Point(0.0,0.0), "DLH123", 1);
		assert(aircraft.getFootprint() != null);
		assert(aircraft.getFootprint().getId() == 0);
		assert(aircraft.getFootprint().getRole() == AgentRole.Civil);
		assert(aircraft.getFootprint().getType().compareTo("aircraft") == 0);
		assert(aircraft.getFootprint().getName().compareTo("DLH123") == 0);
		assert(aircraft.getFootprint().getPosition().equals(new Point(0.0,0.0)));
	}

	@Test
	public void testSimulate() {
		Aircraft aircraft = new Aircraft(0, new Point(0.0,0.0), "DLH123", 2);
		
		//1. no one waiting for aircraft
		Message[] messages1 = aircraft.simulate(0, new Message[] {}, new AgentFootprint[] {});
		assert(messages1.length == 0);
		
		//2. two waiting for aircraft one is called
		BoardingRequest request1 = new BoardingRequest(0,1,0);
		BoardingRequest request2 = new BoardingRequest(0,2,0);
		Message[] messages2 = aircraft.simulate(1, new Message[] {request1, request2}, new AgentFootprint[] {});
		assert(messages2.length == 1);
		assert(messages2[0] instanceof BoardingCall);
		assert(messages2[0].getReceiver() == 1);
		
		//3. one waiting, one called
		Message[] messages3 = aircraft.simulate(1, new Message[] {request1, request2}, new AgentFootprint[] {});
		assert(messages3.length == 1);
		assert(messages3[0] instanceof BoardingCall);
		assert(messages3[0].getReceiver() == 2);
		
		//4. aircraft done
		assert(aircraft.isDone());
		
	}

	@Test
	public void testIsDone() {
		Aircraft aircraft1 = new Aircraft(0, new Point(0.0,0.0), "DLH123", 1);
		assert(!aircraft1.isDone());
		
		Aircraft aircraft2 = new Aircraft(0, new Point(0.0,0.0), "DLH124", 0);
		assert(aircraft2.isDone());
		
	}

}
