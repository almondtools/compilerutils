package net.amygdalum.util.map;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.charArrayContaining;
import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import net.amygdalum.util.map.CharLongMap.Entry;


public class CharLongMapTest {

	private long DEFAULT_VALUE = 42l;

	@Test
	public void testGetDefaultValue() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE);
		assertThat(map.getDefaultValue(), equalTo(42l));
	}

	@Test
	public void testGetForEmptyMap() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE);
		assertThat(map.get((char) 0), equalTo(42l));
		assertThat(map.get('x'), equalTo(42l));
	}

	@Test
	public void testGetForOneElement() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE)
			.add('a', 43l);
		assertThat(map.get('a'), equalTo(43l));
		assertThat(map.get('x'), equalTo(42l));
	}

	@Test
	public void testGetForMoreElements() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE)
			.add('a', 43l)
			.add('b', 44l);
		assertThat(map.get('a'), equalTo(43l));
		assertThat(map.get('b'), equalTo(44l));
		assertThat(map.get('x'), equalTo(42l));
	}

	@Test
	public void testSize() throws Exception {
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add('a', 43)
			.size(), equalTo(1));
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add('a', 43)
			.add('b', 42)
			.size(), equalTo(2));
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add((char) 0, 41)
			.add('b', 42)
			.size(), equalTo(2));
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add((char) 0, DEFAULT_VALUE)
			.add('b', 42)
			.size(), equalTo(1));
	}

	@Test
	public void testKeysEmpty() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE);
		assertThat(map.keys().length, equalTo(0));
	}

	@Test
	public void testKeys() throws Exception {
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add('a', 43)
			.keys(), charArrayContaining('a').inAnyOrder());
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add('a', 43)
			.add('b', 42)
			.keys(), charArrayContaining('a', 'b').inAnyOrder());
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add((char) 0, 41)
			.add('b', 42)
			.keys(), charArrayContaining((char) 0, 'b').inAnyOrder());
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add((char) 0, DEFAULT_VALUE)
			.add('b', 42)
			.keys(), charArrayContaining('b').inAnyOrder());
	}

	@Test
	public void testGetNoKey() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE);

		assertThat(map.get('a'), equalTo(DEFAULT_VALUE));
	}

	@Test
	public void testPutOrdinaryKey() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE);

		map.put('a', 33);

		assertThat(map.get('a'), equalTo(33l));
	}

	@Test
	public void testPutNullKey() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE);

		map.put((char) 0, 33);

		assertThat(map.get((char) 0), equalTo(33l));
	}

	@Test
	public void testPutCollidingKeys() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE) {
			@Override
			public int hash(int key) {
				return 0;
			}
		};

		map.put('a', 88);
		map.put('b', 111);

		assertThat(map.get('a'), equalTo(88l));
		assertThat(map.get('b'), equalTo(111l));
	}

	@Test
	public void testPutTriggersExpansion() throws Exception {
		CharLongMap map = new CharLongMap(0, 0.75f, DEFAULT_VALUE);

		map.put('a', 88);
		map.put('b', 111);

		assertThat(map.get('a'), equalTo(88l));
		assertThat(map.get('b'), equalTo(111l));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.toString(), containsPattern("{*}"));
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add('a', 43).toString(), containsPattern("{*'a'*:*43*}"));
		assertThat(new CharLongMap(DEFAULT_VALUE)
			.add('a', 43).add((char) 0, 88).toString(), allOf(
				containsPattern("{*'a'*:*43*}"),
				containsPattern("{*'" + ((char) 0) + "':*88*}")));
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorHasNextWithConcurrentModification() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE)
			.add('a', 43)
			.add('b', 44);
		Iterator<Entry> iterator = map.cursor().iterator();

		map.add('c', 45);
		iterator.hasNext();
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorNextWithConcurrentModification() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE)
			.add('a', 43)
			.add('b', 44);
		Iterator<Entry> iterator = map.cursor().iterator();

		map.add('c', 45);
		iterator.next();
	}

	@Test
	public void testCursorRemove() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE)
			.add('a', 43);
		Iterator<Entry> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get('a'), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test
	public void testCursorRemoveNull() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE)
			.add((char) 0, 43);
		Iterator<Entry> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get((char) 0), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test(expected=NoSuchElementException.class)
	public void testCursorNextBeyondEnd() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE)
			.add('a', 43);
		Iterator<Entry> iterator = map.cursor().iterator();
		iterator.next();

		iterator.next();
	}

	@Test
	public void testCursorRemove2() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE)
			.add('a', 43);
		Iterator<Entry> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();
		iterator.remove();

		assertThat(map.get('a'), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test(expected=NoSuchElementException.class)
	public void testCursorRemoveInitial() throws Exception {
		CharLongMap map = new CharLongMap(DEFAULT_VALUE)
			.add('a', 43);
		Iterator<Entry> iterator = map.cursor().iterator();

		iterator.remove();
	}

}
