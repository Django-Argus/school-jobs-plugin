package net.argus.school.plugin.jobs;

import java.util.Map;

public class AttributionEntry<K, V> implements Map.Entry<K, V> {
	
	private K key;
	private V value;

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}
	
	public K setKey(K key) {
		this.key = key;
		return key;
	}
	
	@Override
	public String toString() {
		return key + "=" + value;
	}
	
}
