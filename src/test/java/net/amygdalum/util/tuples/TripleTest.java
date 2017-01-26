package net.amygdalum.util.tuples;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TripleTest {

	@Test
	public void testTriple() throws Exception {
		assertThat(new Triple<>("A", "B", "C"), satisfiesDefaultEquality()
			.andEqualTo(new Triple<>("A", "B", "C"))
			.andNotEqualTo(new Triple<>("C", "D", "E"))
			.andNotEqualTo(new Triple<>("X", "B", "C"))
			.andNotEqualTo(new Triple<>("A", "X", "C"))
			.andNotEqualTo(new Triple<>("A", "B", "X"))
			.andNotEqualTo(new Triple<>(null, "B", "C"))
			.andNotEqualTo(new Triple<>("A", null, "C"))
			.andNotEqualTo(new Triple<>("A", "B", null))
			.andNotEqualTo(new Triple<>(null, null, "C"))
			.andNotEqualTo(new Triple<>("A", null, null))
			.andNotEqualTo(new Triple<>(null, "B", null))
			.andNotEqualTo(new Triple<>(null, null, null))
			.includingToString());
	}

}
