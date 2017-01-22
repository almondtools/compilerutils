package net.amygdalum.util.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Arrays<T> {

	private List<T> base;

	private Arrays(List<T> base) {
		this.base = base;
	}

	public static <T> Arrays<T> init(int size) {
		return new Arrays<T>(new ArrayList<T>(size));
	}

	public static char[] fromWrapped(Collection<Character> charlist) {
		char[] chars = new char[charlist.size()];
		int i = 0;
		for (Character c : charlist) {
			chars[i] = c.charValue();
			i++;
		}
		return chars;
	}

	public Arrays<T> add(T item) {
		base.add(item);
		return this;
	}

	public Arrays<T> addAll(List<T> items) {
		base.addAll(items);
		return this;
	}

	@SuppressWarnings("unchecked")
	public T[] build() {
		return (T[]) base.toArray();
	}

}
