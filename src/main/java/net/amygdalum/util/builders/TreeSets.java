package net.amygdalum.util.builders;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class TreeSets<T> {

	private TreeSet<T> set;

	private TreeSets() {
		this.set = new TreeSet<T>();
	}

	private TreeSets(Comparator<? super T> comparator) {
		this.set = new TreeSet<T>(comparator);
	}

	private TreeSets(Collection<? extends T> set) {
		this.set = new TreeSet<T>(set);
	}

	private TreeSets(Collection<? extends T> set, Comparator<? super T> comparator) {
		this.set = new TreeSet<T>(comparator);
		this.set.addAll(set);
	}

	public static <T> TreeSets<T> sorted() {
		return new TreeSets<T>();
	}

	public static <T> TreeSets<T> sorted(Comparator<? super T> comparator) {
		return new TreeSets<T>(comparator);
	}

	public static <T> TreeSets<T> sorted(Collection<T> set) {
		return new TreeSets<T>(set);
	}

	public static <T> TreeSets<T> empty() {
		return new TreeSets<T>();
	}

	public static <T> TreeSets<T> empty(Comparator<? super T> comparator) {
		return new TreeSets<T>(comparator);
	}

	@SafeVarargs
	public static <T> TreeSet<T> of(T... elements) {
		return new TreeSet<T>(Arrays.asList(elements));
	}

	@SafeVarargs
	public static <T> TreeSet<T> of(Predicate<T> cond, T... elements) {
		TreeSet<T> list = new TreeSet<T>();
		for (T element : elements) {
			if (cond.evaluate(element)) {
				list.add(element);
			}
		}
		return list;
	}

	public static <T> TreeSet<T> intersectionOf(Set<T> set, Set<T> other) {
		return new TreeSets<T>(set).intersect(other).build();
	}

	public static <T> TreeSet<T> unionOf(Set<T> set, Set<T> other) {
		return new TreeSets<T>(set).union(other).build();
	}

	public static <T> TreeSet<T> complementOf(Set<T> set, Set<T> minus) {
		return new TreeSets<T>(set).minus(minus).build();
	}

	public TreeSets<T> union(Set<T> add) {
		return addAll(add);
	}

	public TreeSets<T> add(T add) {
		set.add(add);
		return this;
	}

	public TreeSets<T> addConditional(boolean b, T add) {
		if (b) {
			set.add(add);
		}
		return this;
	}

	public TreeSets<T> addAll(Set<T> add) {
		set.addAll(add);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TreeSets<T> addAll(T... add) {
		set.addAll(Arrays.asList(add));
		return this;
	}

	public TreeSets<T> minus(Set<T> remove) {
		return removeAll(remove);
	}

	public TreeSets<T> remove(T remove) {
		set.remove(remove);
		return this;
	}

	public TreeSets<T> removeConditional(boolean b, T remove) {
		if (b) {
			set.remove(remove);
		}
		return this;
	}

	public TreeSets<T> removeAll(Set<T> remove) {
		set.removeAll(remove);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TreeSets<T> removeAll(T... remove) {
		set.removeAll(Arrays.asList(remove));
		return this;
	}

	public TreeSets<T> intersect(Set<T> retain) {
		return retainAll(retain);
	}

	public TreeSets<T> retain(T retain) {
		Set<T> retainAll = new HashSet<T>();
		retainAll.add(retain);
		set.retainAll(retainAll);
		return this;
	}

	public TreeSets<T> retainConditional(boolean b, T retain) {
		if (b) {
			Set<T> retainAll = new HashSet<T>();
			retainAll.add(retain);
			set.retainAll(retainAll);
		}
		return this;
	}

	public TreeSets<T> retainAll(Set<T> retain) {
		set.retainAll(retain);
		return this;
	}

	@SuppressWarnings("unchecked")
	public TreeSets<T> retainAll(T... retain) {
		set.retainAll(Arrays.asList(retain));
		return this;
	}

	public TreeSet<T> build() {
		return set;
	}

}
