package net.amygdalum.util.builders;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Maps<K,V> {

	private Map<K,V> map;

	private Maps(boolean linked) {
		if (linked) {
			this.map = new LinkedHashMap<K, V>();
		} else {
			this.map = new HashMap<K, V>();
		}
	}

	public Maps<K,V> put(K key, V value) {
		map.put(key, value);
		return this;
	}

	public static <K,V> Maps<K, V> linked() {
		return new Maps<K,V>(true);
	}

	public static <K,V> Maps<K, V> hashed() {
		return new Maps<K,V>(false);
	}

	public static <K,V> Maps<K, V> invert(Map<V, K> toinvert) {
		Maps<K, V> maps = new Maps<K,V>(false);
		for (Map.Entry<V,K> entry: toinvert.entrySet()) {
			maps.put(entry.getValue(), entry.getKey());
		}
		return maps;
	}

	public Map<K,V> build() {
		return map;
	}

}
