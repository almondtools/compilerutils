package net.amygdalum.util.tuples;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PairTest {

	@Test
	public void testPair() throws Exception {
		assertThat(new Pair<>("A", "B"), satisfiesDefaultEquality()
			.andEqualTo(new Pair<>("A", "B"))
			.andNotEqualTo(new Pair<>("C", "D"))
			.andNotEqualTo(new Pair<>("A", "C"))
			.andNotEqualTo(new Pair<>("C", "B"))
			.andNotEqualTo(new Pair<>("A", null))
			.andNotEqualTo(new Pair<>(null, "B"))
			.includingToString());
	}

}
