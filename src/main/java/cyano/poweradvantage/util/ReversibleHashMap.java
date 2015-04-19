package cyano.poweradvantage.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ReversibleHashMap<K,V> implements ReversibleMap<K,V>{
	
	private final java.util.HashMap<K,V> forward;
	private final java.util.HashMap<V,K> reverse;
	
	public ReversibleHashMap(){
		forward = new java.util.HashMap<K,V>();
		reverse = new java.util.HashMap<V,K>();
	}

	@Override
	public int size() {
		assert forward.size() == reverse.size();
		return forward.size();
	}

	@Override
	public boolean isEmpty() {
		assert forward.isEmpty() == reverse.isEmpty();
		return forward.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return forward.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return reverse.containsKey(value);
	}

	@Override
	public V get(Object key) {
		return forward.get(key);
	}

	@Override
	public V put(K key, V value) {
		V prev = forward.get(key);
		forward.put(key, value);
		reverse.put(value,key);
		return prev;
	}

	@Override
	public V remove(Object key) {
		if(this.containsKey(key)){
			V prev = forward.get(key);
			forward.remove(key);
			reverse.remove(prev);
			return prev;
		} else {
			return null;
		}
	}

	@Override
	public K removeValue(V value) {
		if(reverse.containsKey(value)){
			K prev = reverse.get(value);
			reverse.remove(value);
			forward.remove(prev);
			return prev;
		} else {
			return null;
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m){
		for(Map.Entry<? extends K,? extends V> pair : m.entrySet()){
			this.put(pair.getKey(), pair.getValue());
		}
	}

	@Override
	public void clear() {
		forward.clear();
		reverse.clear();
	}

	@Override
	public Set<K> keySet() {
		return forward.keySet();
	}

	@Override
	public Collection<V> values() {
		return valueSet();
	}

	@Override
	public Set<Map.Entry<K,V>> entrySet() {
		return forward.entrySet();
	}

	@Override
	public K getKey(Object value) {
		return reverse.get(value);
	}

	@Override
	public Set<V> valueSet() {
		return reverse.keySet();
	}

}
