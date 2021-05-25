package aas.controller.export;

import aas.model.Agent;
import aas.model.communication.Message;

public interface Export {
	
	public void notify(int time, Agent agent, Message[] messages);
		
	public void finish();
	
}
