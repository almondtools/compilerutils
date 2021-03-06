package net.amygdalum.util.map;

import static java.util.Arrays.sort;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharObjectMap<T> extends TuneableMap {

	private static final char NULL_KEY = 0;

	private float loadFactor;
	private int mask;
	private int expandAt;
	private int size;

	private char[] keys;
	private T[] values;
	private T defaultValue;
	private T nullValue;

	public CharObjectMap(T defaultValue) {
		this(DEFAULT_SIZE, DEFAULT_LOAD, defaultValue);
	}

	@SuppressWarnings("unchecked")
	public CharObjectMap(int initialSize, float loadFactor, T defaultValue) {
		this.loadFactor = loadFactor;
		this.mask = mask(initialSize, loadFactor);
		this.expandAt = initialSize;
		this.size = 0;
		this.keys = new char[mask + 1];
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

	public char[] keys() {
		int size = this.size;
		if (nullValue != defaultValue) {
			size++;
		}
		char[] keys = new char[size];
		int pos = 0;
		for (char c : this.keys) {
			if (c != NULL_KEY) {
				keys[pos] = c;
				pos++;
			}
		}
		if (nullValue != defaultValue) {
			keys[pos] = NULL_KEY;
		}
		sort(keys);
		return keys;
	}

	public CharObjectMap<T> add(char key, T value) {
		put(key, value);
		return this;
	}

	public void put(char key, T value) {
		if (key == NULL_KEY) {
			nullValue = value;
			return;
		}
		int slot = hash(key) & mask;
		while (keys[slot] != key && keys[slot] != NULL_KEY) {
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

	public T get(char key) {
		if (key == NULL_KEY) {
			return nullValue;
		}
		int slot = hash(key) & mask;
		while (keys[slot] != key && keys[slot] != NULL_KEY) {
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

		char[] oldkeys = this.keys;
		T[] oldvalues = this.values;

		char[] keys = new char[mask + 1];
		T[] values = (T[]) new Object[mask + 1];

		int[] delayed = new int[this.size];
		int pos = 0;

		for (int i = 0; i < oldkeys.length; i++) {
			char key = oldkeys[i];
			if (key != NULL_KEY) {
				T value = oldvalues[i];
				int slot = hash(key) & mask;
				if (keys[slot] == NULL_KEY) {
					keys[slot] = key;
					values[slot] = value;
				} else {
					delayed[pos] = i;
					pos++;
				}
			}
		}
		for (int i = 0; i <= pos; i++) {
			int j = delayed[i];
			char key = oldkeys[j];
			T value = oldvalues[j];
			int slot = hash(key) & mask;
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

		private CharObjectMap<T> map;

		public EntryIterable(CharObjectMap<T> map) {
			this.map = map;
		}

		@Override
		public Iterator<Entry<T>> iterator() {
			return new EntryIterator<T>(map);
		}
	}

	public static class EntryIterator<T> implements Iterator<Entry<T>> {

		private CharObjectMap<T> map;
		private int index;
		private int currentKey;
		private int fixedSize;
		private Entry<T> entry;

		public EntryIterator(CharObjectMap<T> map) {
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
				char c = map.keys[currentKey];
				if (c != NULL_KEY) {
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

		public char key;
		public T value;

		@Override
		public String toString() {
			String keystr = "'" + key + "'";
			return new StringBuilder()
				.append(keystr)
				.append(':')
				.append(value)
				.toString();
		}

	}

}
