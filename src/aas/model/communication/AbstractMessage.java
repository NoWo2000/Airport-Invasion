/**
 * 
 */
package aas.model.communication;

/**
 * @author schier
 *
 */
public abstract class AbstractMessage implements Message {
	
	private long time = 0L;
	private Integer sender;
	private Integer receiver;
	

	/**
	 * @param time
	 * @param sender
	 * @param receiver
	 */
	public AbstractMessage(long time, Integer sender, Integer receiver) {
		super();
		this.time = time;
		this.sender = sender;
		this.setReceiver(receiver);
	}

	@Override
	public long getTime() {
		return this.time;
	}

	@Override
	public Integer getSender() {
		return this.sender;
	}

	@Override
	public Integer getReceiver() {
		return this.receiver;
	}

	protected void setReceiver(Integer receiver) {
		this.receiver = receiver;
	}
}
