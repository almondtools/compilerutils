package net.amygdalum.util.builders;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public final class Sets<T> {

	private Set<T> set;

	private Sets(boolean linked) {
		if (linked) {
			this.set = new LinkedHashSet<T>();
		} else {
			this.set = new HashSet<T>();
		}
	}

	private Sets(Collection<? extends T> set, boolean linked) {
		if (linked) {
			this.set = new LinkedHashSet<T>(set);
		} else {
			this.set = new HashSet<T>(set);
		}
	}

	@SafeVarargs
	public static <T> Sets<T> linked(T... elements) {
		if (elements.length == 0) {
			return new Sets<T>(true);
		}
		return new Sets<T>(asList(elements), true);
	}

	public static <T> Sets<T> linked(Collection<T> set) {
		return new Sets<T>(set, true);
	}

	@SafeVarargs
	public static <T> Sets<T> hashed(T... elements) {
		if (elements.length == 0) {
			return new Sets<T>(false);
		}
		return new Sets<T>(asList(elements), false);
	}

	public static <T> Sets<T> hashed(Collection<T> set) {
		return new Sets<T>(set, false);
	}

	@SafeVarargs
	public static <T> Set<T> of(T... elements) {
		return new HashSet<T>(asList(elements));
	}

	@SafeVarargs
	public static <T> Set<T> of(Predicate<T> cond, T... elements) {
		HashSet<T> list = new HashSet<T>();
		for (T element : elements) {
			if (cond.evaluate(element)) {
				list.add(element);
			}
		}
		return list;
	}
	
	@SafeVarargs
	public static <T> Set<T> ofLinked(T... elements) {
		return new LinkedHashSet<T>(asList(elements));
	}

	@SafeVarargs
	public static <T> Set<T> ofLinked(Predicate<T> cond, T... elements) {
		LinkedHashSet<T> list = new LinkedHashSet<T>();
		for (T element : elements) {
			if (cond.evaluate(element)) {
				list.add(element);
			}
		}
		return list;
	}
	
	public static Set<Integer> ofPrimitives(int... array) {
		HashSet<Integer> set = new HashSet<Integer>(array.length);
		for (int i : array) {
			set.add(i);
		}
		return set;
	}

	public static Set<Integer> ofLinkedPrimitives(int... array) {
		LinkedHashSet<Integer> set = new LinkedHashSet<Integer>(array.length);
		for (int i : array) {
			set.add(i);
		}
		return set;
	}

	public static <T> Set<T> intersectionOf(Set<T> set, Set<T> other) {
		return new Sets<T>(set, false).intersect(other).build();
	}

	public static <T> Set<T> unionOf(Set<T> set, Set<T> other) {
		return new Sets<T>(set, false).union(other).build();
	}

	public static <T> Set<T> complementOf(Set<T> set, Set<T> minus) {
		return new Sets<T>(set, false).minus(minus).build();
	}

	public Sets<T> union(Set<T> add) {
		return addAll(add);
	}

	public Sets<T> add(T add) {
		set.add(add);
		return this;
	}

	public Sets<T> addConditional(boolean b, T add) {
		if (b) {
			set.add(add);
		}
		return this;
	}

	public Sets<T> addAll(Set<T> add) {
		set.addAll(add);
		return this;
	}

	@SuppressWarnings("unchecked")
	public Sets<T> addAll(T... add) {
		set.addAll(asList(add));
		return this;
	}

	public Sets<T> minus(Set<T> remove) {
		return removeAll(remove);
	}

	public Sets<T> remove(T remove) {
		set.remove(remove);
		return this;
	}

	public Sets<T> removeConditional(boolean b, T remove) {
		if (b) {
			set.remove(remove);
		}
		return this;
	}

	public Sets<T> removeAll(Set<T> remove) {
		set.removeAll(remove);
		return this;
	}

	@SuppressWarnings("unchecked")
	public Sets<T> removeAll(T... remove) {
		set.removeAll(asList(remove));
		return this;
	}

	public Sets<T> intersect(Set<T> retain) {
		return retainAll(retain);
	}

	public Sets<T> retain(T retain) {
		Set<T> retainAll = new HashSet<T>();
		retainAll.add(retain);
		set.retainAll(retainAll);
		return this;
	}

	public Sets<T> retainConditional(boolean b, T retain) {
		if (b) {
			Set<T> retainAll = new HashSet<T>();
			retainAll.add(retain);
			set.retainAll(retainAll);
		}
		return this;
	}

	public Sets<T> retainAll(Set<T> retain) {
		set.retainAll(retain);
		return this;
	}

	@SuppressWarnings("unchecked")
	public Sets<T> retainAll(T... retain) {
		set.retainAll(asList(retain));
		return this;
	}

	public Set<T> build() {
		return set;
	}

}
