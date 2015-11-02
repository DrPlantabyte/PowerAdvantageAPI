package cyano.poweradvantage.api;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import cyano.poweradvantage.util.HashCodeHelper;

/**
 * This class is used to identify different types of power (or other transport). It is optimized for 
 * high-performance type-comparisons, especially if you use the 
 * <b>areSameType(ConduitType a, ConduitType b)</b> static function.
 * @author DrCyano
 *
 */
public class ConduitType {

	/** type identifier */
	private final String type;
	/** cached hash-code for high-performance type checking */
	private final long hashCache;
	/**
	 * Constructor for ConduitType. The type of a conductor is described by a simple string, such as 
	 * "steam" or "electricity" or "fluid". Convention is to use the noun that describes what is 
	 * moving from the source to the destination. Note that names with identical hashCodes will be 
	 * behave as being the same type (this is a side-effect of performance optimizations).
	 * @param name The name of this power type.
	 */
	public ConduitType(String name){
		type = name.toLowerCase(Locale.US);
		hashCache = HashCodeHelper.stringHashCode(type);
	}
	
	/**
	 * Faster hash-code implementation that relies on cached value
	 */
	@Override
	public int hashCode(){
		return (int)hashCache;
	}
	/**
	 * High-performance equals (fast response for un-equal values) that 
	 * relies on cached hash-codes
	 */
	@Override
	public boolean equals(Object o){
		if(o == null) return false;
		if(this == o) return true;
		if(this.hashCode() == o.hashCode()){ // optimization with cached hashCodes
			if(o instanceof ConduitType){
				return areSameType(this,(ConduitType)o);
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
	public static boolean areSameType(ConduitType a, ConduitType b){
		return a.hashCache == b.hashCache;
	}
	

	/**
	 * High-performance convenience method for comparing conductor types
	 * @param a a PowerConductorInstance
	 * @param b the name of an energy type (e.g. "electricity" or "steam")
	 * @return true if both are the same type of power conductor, false 
	 * otherwise
	 */
	public static boolean areSameType(ConduitType a, String b){
		return a.hashCache == HashCodeHelper.stringHashCode(b.toLowerCase(Locale.US));
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
