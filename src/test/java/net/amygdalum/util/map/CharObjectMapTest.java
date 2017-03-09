package net.amygdalum.util.map;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.charArrayContaining;
import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import net.amygdalum.util.map.CharObjectMap.Entry;

public class CharObjectMapTest {

	private String DEFAULT_VALUE = "42";

	@Test
	public void testGetDefaultValue() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE);
		assertThat(map.getDefaultValue(), equalTo(DEFAULT_VALUE));
	}

	@Test
	public void testGetForEmptyMap() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE);
		assertThat(map.get((char) 0), equalTo(DEFAULT_VALUE));
		assertThat(map.get('x'), equalTo(DEFAULT_VALUE));
	}

	@Test
	public void testGetForOneElement() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43");
		assertThat(map.get('a'), equalTo("43"));
		assertThat(map.get('x'), equalTo(DEFAULT_VALUE));
	}

	@Test
	public void testGetForMoreElements() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.add('b', "44");
		assertThat(map.get('a'), equalTo("43"));
		assertThat(map.get('b'), equalTo("44"));
		assertThat(map.get('x'), equalTo(DEFAULT_VALUE));
	}

	@Test
	public void testCursor() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.add('b', "44");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		List<Character> keys = new ArrayList<>();
		while (iterator.hasNext()) {
			keys.add(iterator.next().key);
		}

		assertThat(keys, containsInAnyOrder('a', 'b'));
	}

	@Test
	public void testCursorWithNullValue() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.add('b', "44")
			.add('c', null);
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		List<Character> keys = new ArrayList<>();
		while (iterator.hasNext()) {
			keys.add(iterator.next().key);
		}

		assertThat(keys, containsInAnyOrder('a', 'b', 'c'));
	}

	@Test
	public void testCursorWithNullKey() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.add('b', "44")
			.add((char) 0, "45");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		List<Character> keys = new ArrayList<>();
		while (iterator.hasNext()) {
			keys.add(iterator.next().key);
		}

		assertThat(keys, containsInAnyOrder('a', 'b', (char) 0));
	}

	@Test
	public void testSize() throws Exception {
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.size(), equalTo(1));
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.add('b', "48")
			.size(), equalTo(2));
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add((char) 0, "41")
			.add('b', "48")
			.size(), equalTo(2));
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add((char) 0, DEFAULT_VALUE)
			.add('b', "48")
			.size(), equalTo(1));
	}

	@Test
	public void testKeysEmpty() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE);
		assertThat(map.keys().length, equalTo(0));
	}

	@Test
	public void testKeys() throws Exception {
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.keys(), charArrayContaining('a'));
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.add('b', "48")
			.keys(), charArrayContaining('a', 'b'));
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add((char) 0, "41")
			.add('b', "48")
			.keys(), charArrayContaining((char) 0, 'b'));
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add((char) 0, DEFAULT_VALUE)
			.add('b', "48")
			.keys(), charArrayContaining('b'));
	}

	@Test
	public void testGetNoKey() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE);

		assertThat(map.get('a'), equalTo(DEFAULT_VALUE));
	}

	@Test
	public void testPutOrdinaryKey() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE);

		map.put('a', "a");

		assertThat(map.get('a'), equalTo("a"));
	}

	@Test
	public void testPutNullKey() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE);

		map.put((char) 0, "a");

		assertThat(map.get((char) 0), equalTo("a"));
	}

	@Test
	public void testPutCollidingKeys() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE) {
			@Override
			public int hash(int key) {
				return 0;
			}
		};

		map.put('a', "a");
		map.put('b', "b");

		assertThat(map.get('a'), equalTo("a"));
		assertThat(map.get('b'), equalTo("b"));
	}

	@Test
	public void testPutTriggersExpansion() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(0, 0.75f, DEFAULT_VALUE);

		map.put('a', "a");
		map.put('b', "b");

		assertThat(map.get('a'), equalTo("a"));
		assertThat(map.get('b'), equalTo("b"));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.toString(), containsPattern("{*}"));
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43").toString(), containsPattern("{*'a'*:*43*}"));
		assertThat(new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43").add((char) 0, "88").toString(), allOf(
				containsPattern("{*'a'*:*43*}"),
				containsPattern("{*'" + ((char) 0) + "':*88*}")));
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorHasNextWithConcurrentModification() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.add('b', "44");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		map.add('c', "45");
		iterator.hasNext();
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorNextWithConcurrentModification() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43")
			.add('b', "44");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		map.add('c', "45");
		iterator.next();
	}

	@Test
	public void testCursorRemove() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get('a'), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test
	public void testCursorRemoveNull() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add((char) 0, "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get((char) 0), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test(expected=NoSuchElementException.class)
	public void testCursorNextBeyondEnd() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.next();
	}

	@Test
	public void testCursorRemove2() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();
		iterator.remove();

		assertThat(map.get('a'), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test(expected=NoSuchElementException.class)
	public void testCursorRemoveInitial() throws Exception {
		CharObjectMap<String> map = new CharObjectMap<String>(DEFAULT_VALUE)
			.add('a', "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		iterator.remove();
	}

}
