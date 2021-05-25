package aas.model.criminal;

import aas.model.Agent;
import aas.model.Moveable;

public interface Braggart extends Agent, Moveable {
	
	public String getFlightToBoard();
	
	public void setFlightToBoard(String flight);

}
