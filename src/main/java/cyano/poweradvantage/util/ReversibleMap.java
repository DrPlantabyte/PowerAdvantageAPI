package cyano.poweradvantage.util;

public interface ReversibleMap<K,V> extends java.util.Map<K,V>{
	
	public abstract K getKey(Object value);

	public abstract java.util.Set<V> valueSet();
	
	public abstract K removeValue(V value);
}
