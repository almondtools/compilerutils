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

import net.amygdalum.util.map.CharIntMap.Entry;

public class CharIntMapTest {

	private int DEFAULT_VALUE = 42;

	@Test
	public void testGetDefaultValue() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE);
		assertThat(map.getDefaultValue(), equalTo(42));
	}

	@Test
	public void testGetForEmptyMap() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE);
		assertThat(map.get((char) 0), equalTo(42));
		assertThat(map.get('x'), equalTo(42));
	}

	@Test
	public void testGetForOneElement() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE)
			.add('a', 43);
		assertThat(map.get('a'), equalTo(43));
		assertThat(map.get('x'), equalTo(42));
	}

	@Test
	public void testGetForMoreElements() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE)
			.add('a', 43)
			.add('b', 44);
		assertThat(map.get('a'), equalTo(43));
		assertThat(map.get('b'), equalTo(44));
		assertThat(map.get('x'), equalTo(42));
	}

	@Test
	public void testSize() throws Exception {
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add('a', 43)
			.size(), equalTo(1));
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add('a', 43)
			.add('b', 42)
			.size(), equalTo(2));
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add((char) 0, 41)
			.add('b', 42)
			.size(), equalTo(2));
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add((char) 0, DEFAULT_VALUE)
			.add('b', 42)
			.size(), equalTo(1));
	}

	@Test
	public void testKeysEmpty() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE);
		assertThat(map.keys().length, equalTo(0));
	}

	@Test
	public void testKeys() throws Exception {
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add('a', 43)
			.keys(), charArrayContaining('a').inAnyOrder());
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add('a', 43)
			.add('b', 42)
			.keys(), charArrayContaining('a','b').inAnyOrder());
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add((char) 0, 41)
			.add('b', 42)
			.keys(), charArrayContaining((char) 0,'b').inAnyOrder());
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add((char) 0, DEFAULT_VALUE)
			.add('b', 42)
			.keys(), charArrayContaining('b').inAnyOrder());
	}

	@Test
	public void testGetNoKey() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE);

		assertThat(map.get('a'), equalTo(DEFAULT_VALUE));
	}

	@Test
	public void testPutOrdinaryKey() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE);

		map.put('a', 33);

		assertThat(map.get('a'), equalTo(33));
	}

	@Test
	public void testPutNullKey() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE);

		map.put((char) 0, 33);

		assertThat(map.get((char) 0), equalTo(33));
	}

	@Test
	public void testPutCollidingKeys() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE) {
			@Override
			public int hash(int key) {
				return 0;
			}
		};

		map.put('a', 88);
		map.put('b', 111);

		assertThat(map.get('a'), equalTo(88));
		assertThat(map.get('b'), equalTo(111));
	}

	@Test
	public void testPutTriggersExpansion() throws Exception {
		CharIntMap map = new CharIntMap(0, 0.75f, DEFAULT_VALUE);

		map.put('a', 88);
		map.put('b', 111);

		assertThat(map.get('a'), equalTo(88));
		assertThat(map.get('b'), equalTo(111));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.toString(), containsPattern("{*}"));
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add('a', 43).toString(), containsPattern("{*'a'*:*43*}"));
		assertThat(new CharIntMap(DEFAULT_VALUE)
			.add('a', 43).add((char) 0, 88).toString(), allOf(
				containsPattern("{*'a'*:*43*}"),
				containsPattern("{*'" + ((char) 0) + "':*88*}")));
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorHasNextWithConcurrentModification() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE)
			.add('a', 43)
			.add('b', 44);
		Iterator<Entry> iterator = map.cursor().iterator();

		map.add('c', 45);
		iterator.hasNext();
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorNextWithConcurrentModification() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE)
			.add('a', 43)
			.add('b', 44);
		Iterator<Entry> iterator = map.cursor().iterator();

		map.add('c', 45);
		iterator.next();
	}

	@Test
	public void testCursorRemove() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE)
			.add('a', 43);
		Iterator<Entry> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get('a'), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test
	public void testCursorRemoveNull() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE)
			.add((char) 0, 43);
		Iterator<Entry> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get((char) 0), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test(expected=NoSuchElementException.class)
	public void testCursorNextBeyondEnd() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE)
			.add('a', 43);
		Iterator<Entry> iterator = map.cursor().iterator();
		iterator.next();

		iterator.next();
	}

	@Test
	public void testCursorRemove2() throws Exception {
		CharIntMap map = new CharIntMap(DEFAULT_VALUE)
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
		CharIntMap map = new CharIntMap(DEFAULT_VALUE)
			.add('a', 43);
		Iterator<Entry> iterator = map.cursor().iterator();

		iterator.remove();
	}

}
