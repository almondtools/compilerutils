package net.amygdalum.util.builders;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public final class SortedSets<T> {

	private SortedSet<T> set;

	private SortedSets() {
		this.set = new TreeSet<T>();
	}

	private SortedSets(Comparator<? super T> comparator) {
		this.set = new TreeSet<T>(comparator);
	}

	private SortedSets(Collection<? extends T> set) {
		this.set = new TreeSet<T>(set);
	}

	public static <T> SortedSets<T> tree(Comparator<? super T> comparator) {
		return new SortedSets<T>(comparator);
	}

	@SafeVarargs
	public static <T> SortedSets<T> tree(T... elements) {
		if (elements.length == 0) {
			return new SortedSets<T>();
		}
		return new SortedSets<T>(asList(elements));
	}

	public static <T> SortedSets<T> tree(Collection<T> set) {
		return new SortedSets<T>(set);
	}

	@SafeVarargs
	public static <T> SortedSet<T> of(T... elements) {
		return new TreeSet<T>(Arrays.asList(elements));
	}

	@SafeVarargs
	public static <T> SortedSet<T> of(Predicate<T> cond, T... elements) {
		TreeSet<T> list = new TreeSet<T>();
		for (T element : elements) {
			if (cond.evaluate(element)) {
				list.add(element);
			}
		}
		return list;
	}

	public static SortedSet<Integer> ofPrimitives(int... array) {
		TreeSet<Integer> set = new TreeSet<Integer>();
		for (int i : array) {
			set.add(i);
		}
		return set;
	}

	public static <T> SortedSet<T> intersectionOf(Set<T> set, Set<T> other) {
		return new SortedSets<T>(set).intersect(other).build();
	}

	public static <T> SortedSet<T> unionOf(Set<T> set, Set<T> other) {
		return new SortedSets<T>(set).union(other).build();
	}

	public static <T> SortedSet<T> complementOf(Set<T> set, Set<T> minus) {
		return new SortedSets<T>(set).minus(minus).build();
	}

	public SortedSets<T> union(Set<T> add) {
		return addAll(add);
	}

	public SortedSets<T> add(T add) {
		set.add(add);
		return this;
	}

	public SortedSets<T> addConditional(boolean b, T add) {
		if (b) {
			set.add(add);
		}
		return this;
	}

	public SortedSets<T> addAll(Set<T> add) {
		set.addAll(add);
		return this;
	}

	@SuppressWarnings("unchecked")
	public SortedSets<T> addAll(T... add) {
		set.addAll(Arrays.asList(add));
		return this;
	}

	public SortedSets<T> minus(Set<T> remove) {
		return removeAll(remove);
	}

	public SortedSets<T> remove(T remove) {
		set.remove(remove);
		return this;
	}

	public SortedSets<T> removeConditional(boolean b, T remove) {
		if (b) {
			set.remove(remove);
		}
		return this;
	}

	public SortedSets<T> removeAll(Set<T> remove) {
		set.removeAll(remove);
		return this;
	}

	@SuppressWarnings("unchecked")
	public SortedSets<T> removeAll(T... remove) {
		set.removeAll(Arrays.asList(remove));
		return this;
	}

	public SortedSets<T> intersect(Set<T> retain) {
		return retainAll(retain);
	}

	public SortedSets<T> retain(T retain) {
		Set<T> retainAll = new HashSet<T>();
		retainAll.add(retain);
		set.retainAll(retainAll);
		return this;
	}

	public SortedSets<T> retainConditional(boolean b, T retain) {
		if (b) {
			Set<T> retainAll = new HashSet<T>();
			retainAll.add(retain);
			set.retainAll(retainAll);
		}
		return this;
	}

	public SortedSets<T> retainAll(Set<T> retain) {
		set.retainAll(retain);
		return this;
	}

	@SuppressWarnings("unchecked")
	public SortedSets<T> retainAll(T... retain) {
		set.retainAll(Arrays.asList(retain));
		return this;
	}

	public SortedSet<T> build() {
		return set;
	}

}
