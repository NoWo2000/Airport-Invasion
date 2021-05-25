package aas.model.communication;

public interface Message {
	
	public long getTime();
	
	public Integer getSender();
	
	public Integer getReceiver();
	
	public double getMaximumDistance();

}
