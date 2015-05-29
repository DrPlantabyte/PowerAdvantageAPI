package cyano.poweradvantage.util;

public abstract class HashCodeHelper {

	
	/**
	 * This is the <code>java.lang.String.hashCode()</code> implementation, 
	 * expanded to 64 bits. This method exists to future-proof the generation 
	 * of random number seeds from Strings. If passed a string that can be 
	 * parsed as an integer, that (64-bit) integer value will be returned.
	 * @param string A String object to serve as the seed for the hash code.
	 * @return Returns the hash code of the string using the algorithm that 
	 * became standard in Java 1.2 <code>java.lang.String.hashCode()</code>, 
	 * but expanded out to 64-bit integers. 
	 * 
	 */
	public static long stringHashCode(String string){
		// return number value if number, otherwise use string hash code
		boolean isNumber = true;
		for(int i = 0; i < string.length(); i++){
			if(i == 0 && string.charAt(i) == '-') continue;
			if(Character.isDigit(string.charAt(i)) == false){
				isNumber = false;
				break;
			}
		}
		if(isNumber){
			try{
				return Long.parseLong(string);
			} catch(NumberFormatException ex){
				// not a number, do nothing
			}
		}
		long h = 0;
		for (int i = 0; i < string.length(); i++) {
			h = 31*h + string.charAt(i);
		}
		return h;
	}
}
