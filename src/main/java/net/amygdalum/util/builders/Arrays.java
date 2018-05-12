package net.amygdalum.util.builders;

import java.util.ArrayList;
import java.util.List;

public final class Arrays<T> {

	private List<T> base;

	private Arrays(List<T> base) {
		this.base = base;
	}

	public static <T> Arrays<T> init(int size) {
		return new Arrays<T>(new ArrayList<T>(size));
	}

	public Arrays<T> add(T item) {
		base.add(item);
		return this;
	}

	public Arrays<T> addAll(T[] items) {
		for (T item : items) {
			base.add(item);
		}
		return this;
	}

	public Arrays<T> addAll(List<T> items) {
		base.addAll(items);
		return this;
	}

	public T[] build(T[] a) {
		return base.toArray(a);
	}

}
