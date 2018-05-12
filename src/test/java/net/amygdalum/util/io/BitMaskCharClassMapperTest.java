package net.amygdalum.util.io;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.charArrayContaining;
import static java.lang.Character.MAX_VALUE;
import static java.lang.Character.MIN_VALUE;
import static java.util.Arrays.asList;
import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Test;

import net.amygdalum.util.text.CharRange;

public class BitMaskCharClassMapperTest {

	@Test
	public void testFullRangeCharClass() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(asList(new CharRange(MIN_VALUE, MAX_VALUE)));
		assertThat(mapper.getIndex(MIN_VALUE), equalTo(0));
		assertThat(mapper.getIndex('5'), equalTo(0));
		assertThat(mapper.getIndex('a'), equalTo(0));
		assertThat(mapper.getIndex('ä'), equalTo(0));
		assertThat(mapper.getIndex(MAX_VALUE), equalTo(0));
		assertThat(mapper.indexCount(), equalTo(1));
		assertThat(mapper.representative(0), equalTo(MIN_VALUE));
	}

	@Test
	public void testEmptyRangeCharClass() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(Collections.<CharRange>emptyList());
		assertThat(mapper.getIndex(MIN_VALUE), equalTo(0));
		assertThat(mapper.getIndex('5'), equalTo(0));
		assertThat(mapper.getIndex('a'), equalTo(0));
		assertThat(mapper.getIndex('ä'), equalTo(0));
		assertThat(mapper.getIndex(MAX_VALUE), equalTo(0));
		assertThat(mapper.indexCount(), equalTo(1));
		assertThat(mapper.representative(0), equalTo(MIN_VALUE));
	}

	@Test
	public void testPrefixRangeCharClass() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(asList(new CharRange(MIN_VALUE, 'a')));
		assertThat(mapper.getIndex(MIN_VALUE), equalTo(1));
		assertThat(mapper.getIndex('5'), equalTo(1));
		assertThat(mapper.getIndex('a'), equalTo(1));
		assertThat(mapper.getIndex('ä'), equalTo(0));
		assertThat(mapper.getIndex(MAX_VALUE), equalTo(0));
		assertThat(mapper.indexCount(), equalTo(2));
		assertThat(mapper.representative(0), equalTo(after('a')));
		assertThat(mapper.representative(1), equalTo(MIN_VALUE));
	}

	@Test
	public void testSuffixRangeCharClass() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(asList(new CharRange('a', MAX_VALUE)));
		assertThat(mapper.getIndex(MIN_VALUE), equalTo(0));
		assertThat(mapper.getIndex('5'), equalTo(0));
		assertThat(mapper.getIndex('a'), equalTo(1));
		assertThat(mapper.getIndex('ä'), equalTo(1));
		assertThat(mapper.getIndex(MAX_VALUE), equalTo(1));
		assertThat(mapper.indexCount(), equalTo(2));
		assertThat(mapper.representative(0), equalTo(MIN_VALUE));
		assertThat(mapper.representative(1), equalTo('a'));
	}

	@Test
	public void testInfixRangeCharClass() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(asList(new CharRange('5', 'ä')));
		assertThat(mapper.getIndex(MIN_VALUE), equalTo(0));
		assertThat(mapper.getIndex('5'), equalTo(1));
		assertThat(mapper.getIndex('a'), equalTo(1));
		assertThat(mapper.getIndex('ä'), equalTo(1));
		assertThat(mapper.getIndex(MAX_VALUE), equalTo(0));
		assertThat(mapper.indexCount(), equalTo(2));
		assertThat(mapper.representative(0), equalTo(MIN_VALUE));
		assertThat(mapper.representative(1), equalTo('5'));
	}

	@Test
	public void testInterruptedRangeCharClass() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(asList(new CharRange('5', before('a')), new CharRange(after('a'), 'ä')));
		assertThat(mapper.getIndex(MIN_VALUE), equalTo(0));
		assertThat(mapper.getIndex('5'), equalTo(1));
		assertThat(mapper.getIndex('a'), equalTo(0));
		assertThat(mapper.getIndex('ä'), equalTo(2));
		assertThat(mapper.getIndex(MAX_VALUE), equalTo(0));
		assertThat(mapper.indexCount(), equalTo(3));
		assertThat(mapper.representative(0), equalTo(MIN_VALUE));
		assertThat(mapper.representative(1), equalTo('5'));
		assertThat(mapper.representative(2), equalTo(after('a')));
	}

	@Test
	public void testRepresentatives() throws Exception {
		BitMaskCharClassMapper mapper = new BitMaskCharClassMapper(asList(new CharRange('5', before('a')), new CharRange('b', 'ä')));
		
		assertThat(mapper.representatives(), charArrayContaining(MIN_VALUE, '5','b'));
		
		assertThat(mapper.representative('5'), equalTo('5'));
		assertThat(mapper.representative(before('a')), equalTo('5'));
		
		assertThat(mapper.representative('a'), equalTo(MIN_VALUE));
		
		assertThat(mapper.representative('b'), equalTo('b'));
		assertThat(mapper.representative('ä'), equalTo('b'));
	}

}
