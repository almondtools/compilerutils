package net.amygdalum.util.worklist;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class WorkSetTest {

	@Test
	public void testWorkset() throws Exception {
		assertThat(new WorkSet<>().isEmpty(), is(true));
	}

	@Test
	public void testAdd() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		boolean changed = ws.add("A");

		assertThat(changed, is(true));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), equalTo("A"));
		assertThat(ws.size(), equalTo(1));
	}

	@Test
	public void testAddExisting() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");

		boolean changed = ws.add("A");

		assertThat(changed, is(false));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), equalTo("A"));
		assertThat(ws.size(), equalTo(1));
	}

	@Test
	public void testAddDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");
		ws.remove();

		boolean changed = ws.add("A");

		assertThat(changed, is(false));
		assertThat(ws.isEmpty(), is(true));
		assertThat(ws.getDone(), contains("A"));
		assertThat(ws.peek(), nullValue());
		assertThat(ws.size(), equalTo(0));
	}

	@Test
	public void testOffer() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		boolean changed = ws.offer("A");

		assertThat(changed, is(true));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), equalTo("A"));
		assertThat(ws.size(), equalTo(1));
	}

	@Test
	public void testOfferExisting() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");

		boolean changed = ws.offer("A");

		assertThat(changed, is(false));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), equalTo("A"));
		assertThat(ws.size(), equalTo(1));
	}

	@Test
	public void testOfferDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");
		ws.remove();

		boolean changed = ws.offer("A");

		assertThat(changed, is(false));
		assertThat(ws.isEmpty(), is(true));
		assertThat(ws.getDone(), contains("A"));
		assertThat(ws.peek(), nullValue());
		assertThat(ws.size(), equalTo(0));
	}

	@Test
	public void testAddAll() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		boolean changed = ws.addAll(asList("A", "B"));

		assertThat(changed, is(true));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), equalTo("A"));
		assertThat(ws.toArray(new String[0]), arrayContaining("A", "B"));
		assertThat(ws.size(), equalTo(2));
	}


	@Test
	public void testAddAllExisting() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		boolean changed = ws.addAll(asList("A","B"));

		assertThat(changed, is(false));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), equalTo("A"));
		assertThat(ws.toArray(new String[0]), arrayContaining("A", "B"));
		assertThat(ws.size(), equalTo(2));
	}

	@Test
	public void testAddAllSomeExisting() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		boolean changed = ws.addAll(asList("B","C"));

		assertThat(changed, is(true));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), equalTo("A"));
		assertThat(ws.toArray(new String[0]), arrayContaining("A", "B","C"));
		assertThat(ws.size(), equalTo(3));
	}

	@Test
	public void testAddAllSomeDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));
		ws.remove();

		boolean changed = ws.addAll(asList("A","C"));

		assertThat(changed, is(true));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), contains("A"));
		assertThat(ws.peek(), equalTo("B"));
		assertThat(ws.toArray(new String[0]), arrayContaining("B", "C"));
		assertThat(ws.size(), equalTo(2));
	}

	@Test(expected = NoSuchElementException.class)
	public void testRemoveOnEmpty() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		ws.remove();
	}

	@Test
	public void testRemoveEmptying() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");

		String r = ws.remove();

		assertThat(r, equalTo("A"));
		assertThat(ws.isEmpty(), is(true));
		assertThat(ws.getDone(), contains("A"));
		assertThat(ws.peek(), nullValue());
	}

	@Test
	public void testRemoveNonEmptying() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		String r = ws.remove();

		assertThat(r, equalTo("A"));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), contains("A"));
		assertThat(ws.peek(), equalTo("B"));
	}

	@Test
	public void testPollOnEmpty() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		String r = ws.poll();

		assertThat(r, nullValue());
		assertThat(ws.isEmpty(), is(true));
	}

	@Test
	public void testPollEmptying() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.add("A");

		String r = ws.poll();

		assertThat(r, equalTo("A"));
		assertThat(ws.isEmpty(), is(true));
		assertThat(ws.getDone(), contains("A"));
		assertThat(ws.peek(), nullValue());
	}

	@Test
	public void testPollNonEmptying() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		String r = ws.poll();

		assertThat(r, equalTo("A"));
		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), contains("A"));
		assertThat(ws.peek(), equalTo("B"));
	}

	@Test
	public void testContains() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.contains("A"), is(true));
		assertThat(ws.contains("B"), is(true));
		assertThat(ws.contains("C"), is(false));
	}

	@Test
	public void testContainsDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.remove();

		assertThat(ws.contains("A"), is(true));
		assertThat(ws.contains("B"), is(true));
		assertThat(ws.contains("C"), is(false));
	}

	@Test
	public void testIterator() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		Iterator<String> iterator = ws.iterator();

		assertThat(iterator.next(), equalTo("A"));
		assertThat(iterator.next(), equalTo("B"));
	}

	@Test
	public void testToArray() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.toArray(), arrayContaining((Object) "A", "B"));
		assertThat(ws.toArray(new String[0]), arrayContaining("A", "B"));
	}

	@Test
	public void testRemoveObject() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.remove("A");

		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), equalTo("B"));
	}

	@Test
	public void testRemoveObjectOnDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("0", "A", "B"));
		ws.remove();
		ws.remove();

		ws.remove("A");

		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), contains("0"));
		assertThat(ws.peek(), equalTo("B"));
	}

	@Test
	public void testRemoveAll() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.removeAll(asList("A", "B"));

		assertThat(ws.isEmpty(), is(true));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), nullValue());
	}

	@Test
	public void testRemoveAllOnDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("0", "A", "B"));
		ws.remove();
		ws.remove();

		ws.removeAll(asList("A", "B"));

		assertThat(ws.isEmpty(), is(true));
		assertThat(ws.getDone(), contains("0"));
		assertThat(ws.peek(), nullValue());
	}

	@Test
	public void testRetainAll() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.retainAll(asList("B"));

		assertThat(ws.isEmpty(), is(false));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), equalTo("B"));
	}

	@Test
	public void testContainsAll() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.containsAll(asList("B")), is(true));
		assertThat(ws.containsAll(asList("A")), is(true));
		assertThat(ws.containsAll(asList("A", "B")), is(true));
		assertThat(ws.containsAll(asList("B", "A")), is(true));
		assertThat(ws.containsAll(asList("C")), is(false));
		assertThat(ws.containsAll(asList("A", "B", "C")), is(false));
	}

	@Test
	public void testContainsAllDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.remove();

		assertThat(ws.containsAll(asList("B")), is(true));
		assertThat(ws.containsAll(asList("A")), is(true));
		assertThat(ws.containsAll(asList("A", "B")), is(true));
		assertThat(ws.containsAll(asList("B", "A")), is(true));
		assertThat(ws.containsAll(asList("C")), is(false));
		assertThat(ws.containsAll(asList("A", "B", "C")), is(false));
	}

	@Test
	public void testClear() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		ws.clear();

		assertThat(ws.isEmpty(), is(true));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), nullValue());
	}

	@Test
	public void testClearOnDone() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));
		ws.remove();

		ws.clear();

		assertThat(ws.isEmpty(), is(true));
		assertThat(ws.getDone(), empty());
		assertThat(ws.peek(), nullValue());
	}

	@Test(expected = NoSuchElementException.class)
	public void testElementOnEmpty() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		ws.element();
	}

	@Test
	public void testElement() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.element(), equalTo("A"));
	}

	@Test
	public void testPeekOnEmpty() throws Exception {
		WorkSet<String> ws = new WorkSet<>();

		assertThat(ws.peek(), nullValue());
	}

	@Test
	public void testPeek() throws Exception {
		WorkSet<String> ws = new WorkSet<>();
		ws.addAll(asList("A", "B"));

		assertThat(ws.peek(), equalTo("A"));
	}

}
