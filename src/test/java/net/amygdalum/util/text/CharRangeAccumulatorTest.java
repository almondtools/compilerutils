package net.amygdalum.util.text;

import static java.lang.Character.MAX_VALUE;
import static java.lang.Character.MIN_VALUE;
import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class CharRangeAccumulatorTest {

	@Test
	public void testSplitSingleCharacter() throws Exception {
		CharRangeAccumulator acc = new CharRangeAccumulator();
		acc.split('a', 'a');
		List<CharRange> ranges = acc.getRanges();
		assertThat(ranges, contains(
			new CharRange(MIN_VALUE, before('a')),
			new CharRange('a','a'),
			new CharRange(after('a'),MAX_VALUE)));
	}

	@Test
	public void testSplitRange() throws Exception {
		CharRangeAccumulator acc = new CharRangeAccumulator();
		acc.split('a', 'b');
		List<CharRange> ranges = acc.getRanges();
		assertThat(ranges, contains(
			new CharRange(MIN_VALUE, before('a')),
			new CharRange('a','b'),
			new CharRange(after('b'),MAX_VALUE)));
	}

	@Test
	public void testSplitPrefixOverlappingRange() throws Exception {
		CharRangeAccumulator acc = new CharRangeAccumulator();
		acc.split('b', 'c');
		acc.split('a', 'b');
		List<CharRange> ranges = acc.getRanges();
		assertThat(ranges, contains(
			new CharRange(MIN_VALUE, before('a')),
			new CharRange('a','a'),
			new CharRange('b','b'),
			new CharRange('c','c'),
			new CharRange(after('c'),MAX_VALUE)));
	}

	@Test
	public void testSplitSuffixOverlappingRange() throws Exception {
		CharRangeAccumulator acc = new CharRangeAccumulator();
		acc.split('a', 'b');
		acc.split('b', 'c');
		List<CharRange> ranges = acc.getRanges();
		assertThat(ranges, contains(
			new CharRange(MIN_VALUE, before('a')),
			new CharRange('a','a'),
			new CharRange('b','b'),
			new CharRange('c','c'),
			new CharRange(after('c'),MAX_VALUE)));
	}
}