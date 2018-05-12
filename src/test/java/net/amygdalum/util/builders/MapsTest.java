package net.amygdalum.util.builders;

import static com.almondtools.conmatch.datatypes.MapMatcher.noEntries;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.almondtools.conmatch.datatypes.MapMatcher;

public class MapsTest {

	@Test
	public void testFactories() throws Exception {
		assertThat(Maps.hashed().build(), noEntries(Object.class, Object.class));
		assertThat(Maps.linked().build(), noEntries(Object.class, Object.class));
	}

	@Test
	public void testPut() throws Exception {
		Map<String, String> map = Maps.<String, String> hashed()
			.put("key1", "value1")
			.put("key2", "value2")
			.build();

		assertThat(map, MapMatcher.containsEntries(String.class, String.class)
			.entry("key1", "value1")
			.entry("key2", "value2"));
	}

	@Test
	public void testInvert() throws Exception {
		Map<String, String> map = Maps.invert(Maps.<String, String> hashed()
			.put("key1", "value1")
			.put("key2", "value2")
			.build())
			.build();

		assertThat(map, MapMatcher.containsEntries(String.class, String.class)
			.entry("value1", "key1")
			.entry("value2", "key2"));
	}

}
