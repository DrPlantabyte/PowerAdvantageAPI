package cyano.poweradvantage.api;
/**
 * This class is used to identify different types of power. It is optimized for 
 * high-performance type-comparisons, especially if you use the 
 * <b>areSameType(ConductorType a, ConductorType b)</b> static function.
 * @author DrCyano
 *
 */
public class ConductorType {

	/** type identifier */
	private final String type;
	/** cached hash-code for high-performance type checking */
	private final int hashCache;
	/**
	 * Constructor for ConductorType. The type of a conductor is described by 
	 * a simple string, such as "mechanical" or "electrical". Convention is to 
	 * use the adjective that ends with -al if used in the sentence "This 
	 * machine requires __________ power." Note that names with identical 
	 * hashCodes will be behave as being the same type (this is a side-effect of 
	 * performance optimizations).
	 * @param name The name of this power type.
	 */
	public ConductorType(String name){
		type = name;
		hashCache = type.hashCode();
	}
	/**
	 * Faster hash-code implementation that relies on cached value
	 */
	@Override
	public int hashCode(){
		return hashCache;
	}
	/**
	 * High-performance equals (fast response for un-equal values) that 
	 * relies on cached hash-codes
	 */
	@Override
	public boolean equals(Object o){
		if(o == null) return false;
		if(this.hashCode() == o.hashCode()){ // optimization with cached hashCodes
			if(o instanceof ConductorType){
				return type.equals(((ConductorType)o).type);
			}
		}
		return false;
	}
	/**
	 * High-performance convenience method for comparing conductor types
	 * @param a a PowerConductorInstance
	 * @param b another PowerConductorInstance
	 * @return true if both are the same type of power conductor, false 
	 * otherwise
	 */
	public static boolean areSameType(ConductorType a, ConductorType b){
		return a.hashCode() == b.hashCode();
	}
	/**
	 * Returns the energy type name
	 * @return the energy type name
	 */
	@Override
	public String toString(){
		return type;
	}
}
