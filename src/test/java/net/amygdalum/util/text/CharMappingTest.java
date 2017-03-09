package net.amygdalum.util.text;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.charArrayContaining;
import static net.amygdalum.util.text.CharMapping.IDENTITY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CharMappingTest  {

	@Test
	public void testIdentityMap() throws Exception {
		assertThat(IDENTITY.map('b'), charArrayContaining('b'));
		assertThat(IDENTITY.map('c'), charArrayContaining('c'));
	}

	@Test
	public void testIdentityNormalized() throws Exception {
		assertThat(IDENTITY.normalized(new char[]{'a','b','0'}), equalTo(new char[]{'a','b','0'}));
	}
}
