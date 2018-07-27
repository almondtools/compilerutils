package net.amygdalum.util.builders;

import static java.util.Arrays.asList;

import java.util.Iterator;

public final class Iterators {
	
	private Iterators() {
	}

	@SafeVarargs
	public static <T> Iterator<T> of(T... elements) {
		return asList(elements).iterator();
	}

}
