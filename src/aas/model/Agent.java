package aas.model;

import aas.model.communication.Message;

public interface Agent {
	AgentFootprint getFootprint();
	Message[] simulate(int time, Message[] messages, AgentFootprint[] neighbours);
	boolean isDone();
}
