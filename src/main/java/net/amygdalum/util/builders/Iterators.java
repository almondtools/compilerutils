package net.amygdalum.util.builders;

import java.util.Arrays;
import java.util.Iterator;

public final class Iterators {
	
	private Iterators() {
	}

	@SafeVarargs
	public static <T> Iterator<T> of(T... elements) {
		return Arrays.asList(elements).iterator();
	}

}
