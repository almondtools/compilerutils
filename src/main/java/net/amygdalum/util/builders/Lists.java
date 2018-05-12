package net.amygdalum.util.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class Lists<T> {

	private List<T> list;

	private Lists() {
		this.list = new ArrayList<T>();
	}

	private Lists(Collection<? extends T> list) {
		this.list = new ArrayList<T>(list);
	}

	@SafeVarargs
	public static <T> Lists<T> list(T... elements) {
		return new Lists<T>(java.util.Arrays.asList(elements));
	}

	public static <T> Lists<T> empty() {
		return new Lists<T>();
	}

	@SafeVarargs
	public static <T> List<T> of(T... elements) {
		return new ArrayList<T>(java.util.Arrays.asList(elements));
	}

	@SafeVarargs
	public static <T> List<T> ofLinked(T... elements) {
		return new LinkedList<T>(java.util.Arrays.asList(elements));
	}

	@SafeVarargs
	public static <T> List<T> of(Predicate<T> cond, T... elements) {
		ArrayList<T> list = new ArrayList<T>();
		for (T element : elements) {
			if (cond.evaluate(element)) {
				list.add(element);
			}
		}
		return list;
	}

	@SafeVarargs
	public static <T> List<T> ofLinked(Predicate<T> cond, T... elements) {
		LinkedList<T> list = new LinkedList<T>();
		for (T element : elements) {
			if (cond.evaluate(element)) {
				list.add(element);
			}
		}
		return list;
	}
	
	public static List<Integer> ofPrimitives(int[] array) {
		ArrayList<Integer> list = new ArrayList<Integer>(array.length);
		for (int i : array) {
			list.add(i);
		}
		return list;
	}

	public static List<Integer> ofLinkedPrimitives(int[] array) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		for (int i : array) {
			list.add(i);
		}
		return list;
	}
	
	public Lists<T> add(T add) {
		list.add(add);
		return this;
	}

	public Lists<T> addConditional(boolean b, T add) {
		if (b) {
			list.add(add);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public Lists<T> addAll(T... add) {
		list.addAll(java.util.Arrays.asList(add));
		return this;
	}

	public Lists<T> addAll(List<T> add) {
		list.addAll(add);
		return this;
	}

	public Lists<T> remove(T remove) {
		list.remove(remove);
		return this;
	}

	public Lists<T> removeConditional(boolean b, T remove) {
		if (b) {
			list.remove(remove);
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public Lists<T> removeAll(T... remove) {
		list.removeAll(java.util.Arrays.asList(remove));
		return this;
	}

	public Lists<T> removeAll(List<T> remove) {
		list.removeAll(remove);
		return this;
	}

	public Lists<T> retain(T retain) {
		Set<T> retainAll = new HashSet<T>();
		retainAll.add(retain);
		list.retainAll(retainAll);
		return this;
	}

	public Lists<T> retainConditional(boolean b, T retain) {
		if (b) {
			Set<T> retainAll = new HashSet<T>();
			retainAll.add(retain);
			list.retainAll(retainAll);
		}
		return this;
	}

	public Lists<T> retainAll(List<T> retain) {
		list.retainAll(retain);
		return this;
	}

	@SuppressWarnings("unchecked")
	public Lists<T> retainAll(T... retain) {
		list.retainAll(java.util.Arrays.asList(retain));
		return this;
	}

	public List<T> build() {
		return list;
	}

}
