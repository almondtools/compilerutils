package net.amygdalum.util.text;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.intArrayContaining;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringSetTest {

	@Test
	public void testMinLength() throws Exception {
		assertThat(new StringSet(chars("abc", "bcde")).minLength(), equalTo(3));
	}

	@Test
	public void testMaxLength() throws Exception {
		assertThat(new StringSet(chars("abc", "bcde")).maxLength(), equalTo(4));
	}

	@Test
	public void testContainedLengths() throws Exception {
		assertThat(new StringSet(chars("abc", "bcde")).containedLengths(), intArrayContaining(4,3));
	}

	@Test
	public void testContains() throws Exception {
		StringSet stringSet = new StringSet(chars("abc", "bcde"));

		assertThat(stringSet.contains("abc".toCharArray()), is(true));
		assertThat(stringSet.contains("bcde".toCharArray()), is(true));
	}

	private List<char[]> chars(String... string) {
		List<char[]> chars = new ArrayList<>(string.length);
		for (int i = 0; i < string.length; i++) {
			chars.add(string[i].toCharArray());
		}
		return chars;
	}

}
