package net.amygdalum.util.builders;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Iterator;

import org.junit.Test;

public class IteratorsTest {

	@Test
	public void testOf() throws Exception {
		Iterator<String> iterator = Iterators.of("s1","s2");
		assertThat(iterator.next(), equalTo("s1"));
		assertThat(iterator.next(), equalTo("s2"));
	}

}
