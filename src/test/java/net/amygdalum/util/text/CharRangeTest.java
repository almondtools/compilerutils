package net.amygdalum.util.text;

import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.almondtools.conmatch.conventions.EqualityMatcher;

public class CharRangeTest {

	@Test
	public void testCharRange() throws Exception {
		assertThat(new CharRange('q', 't'), EqualityMatcher.satisfiesDefaultEquality()
			.andEqualTo(new CharRange('q', 't'))
			.andNotEqualTo(new CharRange('q', 's'))
			.andNotEqualTo(new CharRange('p', 't'))
			.includingToString());
	}

	@Test
	public void testContains() throws Exception {
		assertThat(new CharRange('a', 'c').contains(before('a')), is(false));
		assertThat(new CharRange('a', 'c').contains('a'), is(true));
		assertThat(new CharRange('a', 'c').contains('b'), is(true));
		assertThat(new CharRange('a', 'c').contains('c'), is(true));
		assertThat(new CharRange('a', 'c').contains(after('c')), is(false));
	}

	@Test
	public void testSplitBefore() throws Exception {
		assertThat(new CharRange('q', 't').splitBefore('s'), contains(new CharRange('q', 'r'), new CharRange('s', 't')));
		assertThat(new CharRange('q', 't').splitBefore('q'), contains(new CharRange('q', 't')));
	}

	@Test
	public void testSplitAfter() throws Exception {
		assertThat(new CharRange('q', 't').splitAfter('s'), contains(new CharRange('q', 's'), new CharRange('t', 't')));
		assertThat(new CharRange('q', 't').splitAfter('t'), contains(new CharRange('q', 't')));
	}
	
	@Test
	public void testSplitAround() throws Exception {
		assertThat(new CharRange('q', 't').splitAround('r','s'), contains(new CharRange('q', 'q'), new CharRange('r', 's'), new CharRange('t', 't')));
		assertThat(new CharRange('q', 't').splitAround('q','s'), contains(new CharRange('q', 's'), new CharRange('t', 't')));
		assertThat(new CharRange('q', 't').splitAround('r','t'), contains(new CharRange('q', 'q'), new CharRange('r', 't')));
		assertThat(new CharRange('q', 't').splitAround('q','t'), contains(new CharRange('q', 't')));
	}
	
}
