package cyano.poweradvantage.api;

/**
 * <p>
 * When a power source receives its power update and it has energy to donate, it will scan the 
 * network of connected conduits to ask the power sinks if they want some energy. The sinks respond 
 * to this offer by returning a PowerRequest object (this class). The PowerRequest instance 
 * specifies the priority of the machine in addition to the amount of energy that the sink needs to 
 * fill its energy buffer. The energy if given out to sinks in order from highest priority to lowest 
 * priority. A standard machine should make MEDIUM_PRIORITY requests, while machines with very small 
 * internal buffers should be HIGH_PRIORITY and machines that have large energy buffers should be 
 * LOW_PRIORITY. Batteries (devices that store energy) should use priority level BACKUP_PRIORITY, 
 * which is even lower priority than LOW_PRIORITY and should only grant PowerRequests of 
 * LOW_PRIORITY or higher. 
 * </p>
 * <p>You can create custom priorities by using numbers (range from -127 to 127). The higher the 
 * number, the higher the priority.</p> 
 * @author DrCyano
 *
 */
public class PowerRequest implements Comparable<PowerRequest>, Cloneable{
	/** amount of energy requested */
	public final float amount;
	/** priority level of the request */
	public final byte priority;
	/** the TileEntity that filed this request */
	public final PoweredEntity entity;

	/**
	 * Lowest possible priority
	 */
	public static final byte LAST_PRIORITY = -127;
	/**
	 * Priority of devices that do nothing but store energy
	 */
	public static final byte BACKUP_PRIORITY = -50;
	/**
	 * Priority of machines that don't need constant energy
	 */
	public static final byte LOW_PRIORITY = 0;
	/**
	 * Normal power request priority
	 */
	public static final byte MEDIUM_PRIORITY = 50;
	/**
	 * Priority for machines that need energy immediately
	 */
	public static final byte HIGH_PRIORITY = 100;
	/**
	 * Highest possible priority
	 */
	public static final byte FIRST_PRIORITY = 127;

	/**
	 * Default PowerRequest object for machines that don't need power of the available type.
	 */
	public static final PowerRequest REQUEST_NOTHING = new PowerRequest(LAST_PRIORITY,0,null);
	/**
	 * Standard constructor for making a power request
	 * @param priority Priority level (determines order of power reception). If demand exceeds 
	 * supply, lower priority machines receive no energy.
	 * @param requestSize How much to ask for of the given energy type.
	 * @param requestSource The TileEntity that created this PowerRequest
	 */
	public PowerRequest(byte priority, float requestSize, PoweredEntity requestSource){
		this.priority = priority;
		this.amount = requestSize;
		this.entity = requestSource;
	}
	
	/**
	 * Standard constructor for making a power request
	 * @param priority Priority level (determines order of power reception). If demand exceeds 
	 * supply, lower priority machines receive no energy.
	 * @param requestSize How much to ask for of the given energy type.
	 * @param requestSource The TileEntity that created this PowerRequest
	 */
	public PowerRequest(int priority, float requestSize, PoweredEntity requestSource){
		this.priority = (byte)priority;
		this.amount = requestSize;
		this.entity = requestSource;
	}
	/**
	 * Copies this this object.
	 * @return A copy of this object
	 */
	@Override
	public PowerRequest clone(){
		return new PowerRequest(priority,amount,entity);
	}

	/**
	 * Compares the PowerRequests such that the higher priority comes before lower priority and 
	 * within the same priority level, machines wanting more power come before those wanting less.
	 */
	@Override
	public int compareTo(PowerRequest o) {
		if(this.priority == o.priority){
			return Float.compare(o.amount, this.amount);
		} else {
			return Byte.compare(o.priority, this.priority);
		}
	}
	/**
	 * toString implementation
	 * @return returns a brief description of the request
	 */
	@Override
	public String toString(){
		return "PowerRequest: "+amount+" units at priority="+((int)priority)+" from entity "+((entity == null)?("null"):(entity.getClass().getSimpleName()));
	}
}
