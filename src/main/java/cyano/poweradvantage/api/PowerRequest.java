package cyano.poweradvantage.api;


public class PowerRequest implements Comparable<PowerRequest>{
	public final float amount;
	public final byte priority;
	public final PoweredEntity entity;

	public static final byte LAST_PRIORITY = -127;
	public static final byte LOW_PRIORITY = 0;
	public static final byte MEDIUM_PRIORITY = 50;
	public static final byte HIGH_PRIORITY = 100;
	public static final byte FIRST_PRIORITY = 127;

	public static final PowerRequest REQUEST_NOTHING = new PowerRequest(LAST_PRIORITY,0,null);
	
	public PowerRequest(byte priority, float requestSize, PoweredEntity requestSource){
		this.priority = priority;
		this.amount = requestSize;
		this.entity = requestSource;
	}
	
	public PowerRequest(int priority, float requestSize, PoweredEntity requestSource){
		this.priority = (byte)priority;
		this.amount = requestSize;
		this.entity = requestSource;
	}

	@Override
	public int compareTo(PowerRequest o) {
		if(this.priority == o.priority){
			return Float.compare(o.amount, this.amount);
		} else {
			return Byte.compare(o.priority, this.priority);
		}
	}
}
