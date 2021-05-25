package aas.model.civil.pax;

import aas.model.AgentFootprint;
import aas.model.communication.Message;
import aas.model.util.Point;

public interface State {
	Point calculateNextStep(int time, Message[] requests, AgentFootprint[] neighbours);
	Message[] getRequests(int time, AgentFootprint me);
	State getNextState();
}
