package net.amygdalum.util.builders;

import static com.almondtools.conmatch.datatypes.ArrayMatcher.arrayContaining;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ArraysTest {

	@Test
	public void testAdd() throws Exception {
		String[] array = Arrays.<String>init(0).add("string").build(new String[0]);
		assertThat(array, arrayContaining(String.class, "string"));
	}

	@Test
	public void testAddAllArray() throws Exception {
		String[] array = Arrays.<String>init(0).addAll(new String[]{"string1","string2"}).build(new String[0]);
		assertThat(array, arrayContaining(String.class, "string1", "string2"));
	}

	@Test
	public void testAddAllList() throws Exception {
		String[] array = Arrays.<String>init(0).addAll(asList("string1","string2")).build(new String[0]);
		assertThat(array, arrayContaining(String.class, "string1", "string2"));
	}
	
}
