package net.amygdalum.util.map;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.byteArrayContaining;
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

import net.amygdalum.util.map.ByteObjectMap.Entry;

public class ByteObjectMapTest {

	private static final byte a = (byte) 0x61;
	private static final byte b = (byte) 0x62;
	private static final byte c = (byte) 0x63;
	private static final byte x = (byte) 0x78;

	private String DEFAULT_VALUE = "42";

	@Test
	public void testGetDefaultValue() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE);
		assertThat(map.getDefaultValue(), equalTo("42"));
	}

	@Test
	public void testGetForEmptyMap() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE);
		assertThat(map.get((byte) 0), equalTo("42"));
		assertThat(map.get(x), equalTo("42"));
	}

	@Test
	public void testGetForOneElement() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43");
		assertThat(map.get(a), equalTo("43"));
		assertThat(map.get((byte) 0x78), equalTo("42"));
	}

	@Test
	public void testGetForMoreElements() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.add(b, "44");
		assertThat(map.get(a), equalTo("43"));
		assertThat(map.get(b), equalTo("44"));
		assertThat(map.get((byte) 0x78), equalTo("42"));
	}

	@Test
	public void testCursor() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.add(b, "44");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		List<Byte> keys = new ArrayList<>();
		while (iterator.hasNext()) {
			keys.add(iterator.next().key);
		}

		assertThat(keys, containsInAnyOrder(a, b));
	}

	@Test
	public void testCursorWithNullValue() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.add(b, "44")
			.add(c, null);
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		List<Byte> keys = new ArrayList<>();
		while (iterator.hasNext()) {
			keys.add(iterator.next().key);
		}

		assertThat(keys, containsInAnyOrder(a, b, c));
	}

	@Test
	public void testCursorWithNullKey() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.add(b, "44")
			.add((byte) 0, "45");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		List<Byte> keys = new ArrayList<>();
		while (iterator.hasNext()) {
			keys.add(iterator.next().key);
		}

		assertThat(keys, containsInAnyOrder(a, b, (byte) 0));
	}

	@Test
	public void testSize() throws Exception {
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.size(), equalTo(1));
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.add(b, "42")
			.size(), equalTo(2));
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add((byte) 0, "41")
			.add(b, "42")
			.size(), equalTo(2));
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add((byte) 0, DEFAULT_VALUE)
			.add(b, "42")
			.size(), equalTo(1));
	}

	@Test
	public void testKeysEmpty() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE);
		assertThat(map.keys().length, equalTo(0));
	}

	@Test
	public void testKeys() throws Exception {
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.keys(), byteArrayContaining(a));
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.add(b, "42")
			.keys(), byteArrayContaining(a, b));
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add((byte) 0, "41")
			.add(b, "42")
			.keys(), byteArrayContaining((byte) 0, b));
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add((byte) 0, DEFAULT_VALUE)
			.add(b, "42")
			.keys(), byteArrayContaining(b));
	}

	@Test
	public void testGetNoKey() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE);

		assertThat(map.get(a), equalTo(DEFAULT_VALUE));
	}

	@Test
	public void testPutOrdinaryKey() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE);

		map.put(a, "a");

		assertThat(map.get(a), equalTo("a"));
	}

	@Test
	public void testPutNullKey() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE);

		map.put((byte) 0, "a");

		assertThat(map.get((byte) 0), equalTo("a"));
	}

	@Test
	public void testPutCollidingKeys() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE) {
			@Override
			public int hash(int key) {
				return 0;
			}
		};

		map.put(a, "a");
		map.put(b, "b");

		assertThat(map.get(a), equalTo("a"));
		assertThat(map.get(b), equalTo("b"));
	}

	@Test
	public void testPutTriggersExpansion() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(0, 0.75f, DEFAULT_VALUE);

		map.put(a, "a");
		map.put(b, "b");

		assertThat(map.get(a), equalTo("a"));
		assertThat(map.get(b), equalTo("b"));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.toString(), containsPattern("{*}"));
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43").toString(), containsPattern("{*" + a + "*:*43*}"));
		assertThat(new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43").add((byte) 0, "88").toString(), allOf(
				containsPattern("{*" + a + "*:*43*}"),
				containsPattern("{*" + ((byte) 0) + "*:*88*}")));
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorHasNextWithConcurrentModification() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.add(b, "44");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		map.add(c, "45");
		iterator.hasNext();
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testCursorNextWithConcurrentModification() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43")
			.add(b, "44");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		map.add(c, "45");
		iterator.next();
	}

	@Test
	public void testCursorRemove() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get(a), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test
	public void testCursorRemoveNull() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add((byte) 0, "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();

		assertThat(map.get((byte) 0), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test(expected=NoSuchElementException.class)
	public void testCursorNextBeyondEnd() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.next();
	}

	@Test
	public void testCursorRemove2() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();
		iterator.next();

		iterator.remove();
		iterator.remove();

		assertThat(map.get(a), equalTo(DEFAULT_VALUE));
		assertThat(map.size(), equalTo(0));
	}

	@Test(expected=NoSuchElementException.class)
	public void testCursorRemoveInitial() throws Exception {
		ByteObjectMap<String> map = new ByteObjectMap<String>(DEFAULT_VALUE)
			.add(a, "43");
		Iterator<Entry<String>> iterator = map.cursor().iterator();

		iterator.remove();
	}

}
