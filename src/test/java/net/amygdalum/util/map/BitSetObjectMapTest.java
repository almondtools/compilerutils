package net.amygdalum.util.map;

import static com.almondtools.conmatch.datatypes.ArrayMatcher.arrayContaining;
import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import net.amygdalum.util.bits.BitSet;
import net.amygdalum.util.map.BitSetObjectMap.Entry;

public class BitSetObjectMapTest {

	private String DEFAULT_VALUE = "42";

	@Test
	public void testGetDefaultValue() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE);
		assertThat(map.getDefaultValue(), equalTo("42"));
	}

	@Test
	public void testGetForEmptyMap() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE);
		assertThat(map.get(bs()), equalTo("42"));
		assertThat(map.get(bs(0, 8, 15)), equalTo("42"));
	}

	@Test
	public void testGetForOneElement() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(1), "43");
		assertThat(map.get(bs(1)), equalTo("43"));
		assertThat(map.get(bs(0, 8, 15)), equalTo("42"));
	}

	@Test
	public void testGetForMoreElements() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(1), "43")
			.add(bs(2, 3, 4), "44");
		assertThat(map.get(bs(1)), equalTo("43"));
		assertThat(map.get(bs(2, 3, 4)), equalTo("44"));
		assertThat(map.get(bs(0, 8, 15)), equalTo("42"));
	}

	private static BitSet bs(int... activeBits) {
		return BitSet.bits(5, activeBits);
	}

	@Test
	public void testSize() throws Exception {
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(1), "43")
			.size(), equalTo(1));
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(1), "43")
			.add(bs(4, 8), "42")
			.size(), equalTo(2));
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(null, "41")
			.add(bs(4, 8), "42")
			.size(), equalTo(2));
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(null, DEFAULT_VALUE)
			.add(bs(4, 8), "42")
			.size(), equalTo(1));
	}

	@Test
	public void testKeysEmpty() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE);
		assertThat(map.keys().length, equalTo(0));
	}

	@Test
	public void testKeys() throws Exception {
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(1), "43")
			.keys(), arrayContaining(BitSet.class, bs(1)).inAnyOrder());
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(1), "43")
			.add(bs(4, 8), "42")
			.keys(), arrayContaining(BitSet.class, bs(1), bs(4, 8)).inAnyOrder());
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(null, "41")
			.add(bs(4, 8), "42")
			.keys(), arrayContaining(BitSet.class, null, bs(4, 8)).inAnyOrder());
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(null, DEFAULT_VALUE)
			.add(bs(4, 8), "42")
			.keys(), arrayContaining(BitSet.class, bs(4, 8)).inAnyOrder());
	}

	@Test
	public void testGetNoKey() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE);

		assertThat(map.get(bs(1)), equalTo(DEFAULT_VALUE));
	}

	@Test
	public void testPutOrdinaryKey() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE);

		map.put(bs(1), "a");

		assertThat(map.get(bs(1)), equalTo("a"));
	}

	@Test
	public void testPutNullKey() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE);

		map.put(null, "a");

		assertThat(map.get(null), equalTo("a"));
	}

	@Test
	public void testPutCollidingKeys() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE) {
			@Override
			public int hash(int key) {
				return 0;
			}
		};

		map.put(bs(22,4), "a");
		map.put(bs(0,0,1), "b");

		assertThat(map.get(bs(22,4)), equalTo("a"));
		assertThat(map.get(bs(0,0,1)), equalTo("b"));
	}

	@Test
	public void testPutTriggersExpansion() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(0, 0.75f, DEFAULT_VALUE);

		map.put(bs(22,4), "a");
		map.put(bs(0,0,1), "b");

		assertThat(map.get(bs(22,4)), equalTo("a"));
		assertThat(map.get(bs(0,0,1)), equalTo("b"));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.toString(), containsPattern("{*}"));
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(43), "43").toString(), containsPattern("{*" + bs(43) + "*:*43*}"));
		assertThat(new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(43), "43").add(null, "88").toString(), allOf(
				containsPattern("{*" + bs(43) + "*:*43*}"),
				containsPattern("{*" + null + "*:*88*}")));
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorHasNextWithConcurrentModification() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(43), "43")
			.add(bs(44), "44");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		map.add(bs(45), "45");
		iterator.hasNext();
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorNextWithConcurrentModification() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(43), "43")
			.add(bs(44), "44");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		map.add(bs(45), "45");
		iterator.next();
	}

	@Test
	public void testCursorRemove() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(43), "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get(bs(43)), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test
	public void testCursorRemoveNull() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(null, "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get(null), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test(expected=NoSuchElementException.class)
	public void testCursorNextBeyondEnd() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(43), "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.next();
	}

	@Test
	public void testCursorRemove2() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(22), "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();
		iterator.remove();

		assertThat(map.get(bs(22)), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test(expected=NoSuchElementException.class)
	public void testCursorRemoveInitial() throws Exception {
		BitSetObjectMap<String> map = new BitSetObjectMap<String>(DEFAULT_VALUE)
			.add(bs(22), "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		iterator.remove();
	}

}
