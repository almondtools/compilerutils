package net.amygdalum.util.map;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.bits.BitSet;

public class BitSetObjectMap<T> extends TuneableMap {

	private static final BitSet NULL_KEY = null;

	private float loadFactor;
	private int mask;
	private int expandAt;
	private int size;

	private BitSet[] keys;
	private T[] values;
	private T defaultValue;
	private T nullValue;

	public BitSetObjectMap(T defaultValue) {
		this(DEFAULT_SIZE, DEFAULT_LOAD, defaultValue);
	}

	@SuppressWarnings("unchecked")
	public BitSetObjectMap(int initialSize, float loadFactor, T defaultValue) {
		this.loadFactor = loadFactor;
		this.mask = mask(initialSize, loadFactor);
		this.expandAt = initialSize;
		this.size = 0;
		this.keys = new BitSet[mask + 1];
		this.values = (T[]) new Object[mask + 1];
		this.defaultValue = defaultValue;
		this.nullValue = defaultValue;
	}

	public int size() {
		int size = this.size;
		if (nullValue != defaultValue) {
			size++;
		}
		return size;
	}

	public BitSet[] keys() {
		int size = this.size;
		if (nullValue != defaultValue) {
			size++;
		}
		BitSet[] keys = new BitSet[size];
		int pos = 0;
		for (BitSet c : this.keys) {
			if (c != NULL_KEY && c != null) {
				keys[pos] = c;
				pos++;
			}
		}
		if (nullValue != defaultValue) {
			keys[pos] = NULL_KEY;
		}
		return keys;
	}

	public BitSetObjectMap<T> add(BitSet key, T value) {
		put(key, value);
		return this;
	}

	public void put(BitSet key, T value) {
		if (key == NULL_KEY) {
			nullValue = value;
			return;
		}
		int slot = hash(key.hashCode()) & mask;
		while (keys[slot] != NULL_KEY && keys[slot] != null && !keys[slot].equals(key)) {
			slot = (slot + 1) & mask;
		}
		if (keys[slot] == NULL_KEY) {
			size++;
		}
		keys[slot] = key;
		values[slot] = value;
		if (size > expandAt) {
			expand(size * 2);
		}
	}

	public T get(BitSet key) {
		if (key == NULL_KEY) {
			return nullValue;
		}
		int slot = hash(key.hashCode()) & mask;
		while (keys[slot] != NULL_KEY && keys[slot] != null && !keys[slot].equals(key)) {
			slot = (slot + 1) & mask;
		}
		if (keys[slot] == NULL_KEY) {
			return defaultValue;
		} else {
			return values[slot];
		}
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public Iterable<Entry<T>> cursor() {
		return new EntryIterable<T>(this);
	}

	@SuppressWarnings("unchecked")
	private void expand(int size) {
		int mask = mask(size, this.loadFactor);

		BitSet[] oldkeys = this.keys;
		T[] oldvalues = this.values;

		BitSet[] keys = new BitSet[mask + 1];
		T[] values = (T[]) new Object[mask + 1];

		int[] delayed = new int[this.size];
		int pos = 0;

		for (int i = 0; i < oldkeys.length; i++) {
			BitSet key = oldkeys[i];
			if (key != NULL_KEY && key != null) {
				T value = oldvalues[i];
				int slot = hash(key.hashCode()) & mask;
				if (keys[slot] == NULL_KEY || keys[slot] == null) {
					keys[slot] = key;
					values[slot] = value;
				} else {
					delayed[pos] = i;
					pos++;
				}
			}
		}
		for (int i = 0; i < pos; i++) {
			int j = delayed[i];
			BitSet key = oldkeys[j];
			T value = oldvalues[j];
			int slot = hash(key.hashCode()) & mask;
			while (keys[slot] != key && keys[slot] != NULL_KEY) {
				slot = (slot + 1) & mask;
			}
			keys[slot] = key;
			values[slot] = value;
		}

		this.expandAt = size;
		this.mask = mask;
		this.keys = keys;
		this.values = values;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("{\n");
		Iterator<Entry<T>> cursor = cursor().iterator();
		if (cursor.hasNext()) {
			Entry<T> entry = cursor.next();
			buffer.append(entry.toString());
		}
		while (cursor.hasNext()) {
			Entry<T> entry = cursor.next();
			buffer.append(",\n").append(entry.toString());
		}
		buffer.append("\n}");
		return buffer.toString();
	}

	public static class EntryIterable<T> implements Iterable<Entry<T>> {

		private BitSetObjectMap<T> map;

		public EntryIterable(BitSetObjectMap<T> map) {
			this.map = map;
		}

		@Override
		public Iterator<Entry<T>> iterator() {
			return new EntryIterator<T>(map);
		}
	}

	public static class EntryIterator<T> implements Iterator<Entry<T>> {

		private BitSetObjectMap<T> map;
		private int index;
		private int currentKey;
		private int fixedSize;
		private Entry<T> entry;

		public EntryIterator(BitSetObjectMap<T> map) {
			this.map = map;
			this.index = 0;
			this.currentKey = -1;
			this.fixedSize = map.size;
			this.entry = new Entry<>();
		}

		@Override
		public boolean hasNext() {
			if (map.size != fixedSize) {
				throw new ConcurrentModificationException();
			}
			return index < fixedSize || index == fixedSize && map.nullValue != map.defaultValue;
		}

		@Override
		public Entry<T> next() {
			if (map.size != fixedSize) {
				throw new ConcurrentModificationException();
			}
			while (currentKey < map.keys.length - 1) {
				currentKey++;
				BitSet b = map.keys[currentKey];
				if (b != NULL_KEY) {
					entry.key = map.keys[currentKey];
					entry.value = map.values[currentKey];
					index++;
					return entry;
				}
			}
			if (map.nullValue != map.defaultValue) {
				entry.key = NULL_KEY;
				entry.value = map.nullValue;
				index++;
				return entry;
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			if (currentKey < 0) {
				throw new NoSuchElementException();
			}
			if (map.keys[currentKey] != NULL_KEY) {
				map.size--;
			} else if (map.values[currentKey] != map.defaultValue) {
				map.nullValue = map.defaultValue;
			}
			map.keys[currentKey] = NULL_KEY;
			map.values[currentKey] = map.defaultValue;
		}
	}

	public static class Entry<T> {

		public BitSet key;
		public T value;

		@Override
		public String toString() {
			String keystr = key == null ? "null" : "0b" + key.toString();
			return new StringBuilder()
				.append(keystr)
				.append(':')
				.append(value)
				.toString();
		}

	}

}
