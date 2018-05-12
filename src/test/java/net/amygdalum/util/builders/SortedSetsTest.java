package net.amygdalum.util.builders;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.util.Comparator;
import java.util.Set;

import org.junit.Test;

public class SortedSetsTest {

	@Test
	public void testFactories() throws Exception {
		assertThat(SortedSets.tree().build(), empty());
		assertThat(SortedSets.tree("string").build(), contains("string"));
		assertThat(SortedSets.tree(asList("string")).build(), contains("string"));
		assertThat(SortedSets.<String>tree(reverse()).addAll("a", "b").build(), contains("b", "a"));
		assertThat(SortedSets.of("string"), contains("string"));
		assertThat(SortedSets.of(numbers(), "string", 42), contains((Object) 42));
		assertThat(SortedSets.ofPrimitives(new int[] { 21, 42 }), contains(21, 42));
		assertThat(SortedSets.ofPrimitives(new int[] { 42, 21 }), contains(21, 42));
	}

	@Test
	public void testIntersection() throws Exception {
		Set<String> intersection = SortedSets.intersectionOf(SortedSets.of("string in intersection", "string1"), SortedSets.of("string2", "string in intersection"));
		assertThat(intersection, contains("string in intersection"));
	}

	@Test
	public void testComplement() throws Exception {
		Set<String> s1 = SortedSets.complementOf(SortedSets.of("string in intersection", "string1"), SortedSets.of("string2", "string in intersection"));
		Set<String> s2 = SortedSets.complementOf(SortedSets.of("string2", "string in intersection"), SortedSets.of("string in intersection", "string1"));
		assertThat(s1, contains("string1"));
		assertThat(s2, contains("string2"));
	}

	@Test
	public void testUnion() throws Exception {
		Set<String> union = SortedSets.unionOf(SortedSets.of("string in intersection", "string1"), SortedSets.of("string2", "string in intersection"));
		assertThat(union, containsInAnyOrder("string1", "string2", "string in intersection"));
	}

	@Test
	public void testAdd() throws Exception {
		Set<String> set = SortedSets.<String> tree()
			.add("string")
			.build();
		assertThat(set, contains("string"));
	}

	@Test
	public void testAddConditional() throws Exception {
		Set<String> set = SortedSets.<String> tree()
			.addConditional(true, "string included")
			.addConditional(false, "string excluded")
			.build();
		assertThat(set, contains("string included"));
	}

	@Test
	public void testAddAllArray() throws Exception {
		Set<String> set = SortedSets.<String> tree()
			.addAll("string1", "string2")
			.build();
		assertThat(set, contains("string1", "string2"));
	}

	@Test
	public void testAddAllList() throws Exception {
		Set<String> set = SortedSets.<String> tree()
			.addAll(SortedSets.of("string2", "string3"))
			.build();
		assertThat(set, contains("string2", "string3"));
	}

	@Test
	public void testRemove() throws Exception {
		Set<String> set = SortedSets.tree("string to remove", "string")
			.remove("string to remove")
			.build();
		assertThat(set, contains("string"));
	}

	@Test
	public void testRemoveConditional() throws Exception {
		Set<String> set = SortedSets.tree("string included", "string excluded")
			.removeConditional(false, "string included")
			.removeConditional(true, "string excluded")
			.build();
		assertThat(set, contains("string included"));
	}

	@Test
	public void testRemoveAllArray() throws Exception {
		Set<String> set = SortedSets.tree("string1", "string2", "string3")
			.removeAll("string1", "string3")
			.build();
		assertThat(set, contains("string2"));
	}

	@Test
	public void testRemoveAllList() throws Exception {
		Set<String> set = SortedSets.tree("string1", "string2", "string3")
			.removeAll(SortedSets.of("string2", "string3"))
			.build();
		assertThat(set, contains("string1"));
	}

	@Test
	public void testRetain() throws Exception {
		Set<String> set = SortedSets.tree("string to retain", "string")
			.retain("string to retain")
			.build();
		assertThat(set, contains("string to retain"));
	}

	@Test
	public void testRetainConditional() throws Exception {
		Set<String> set = SortedSets.tree("string to retain", "string")
			.retainConditional(false, "string")
			.retainConditional(true, "string to retain")
			.build();
		assertThat(set, contains("string to retain"));
	}

	@Test
	public void testRetainAllArray() throws Exception {
		Set<String> set = SortedSets.tree("string1", "string2", "string3")
			.retainAll("string1", "string3")
			.build();
		assertThat(set, contains("string1", "string3"));
	}

	@Test
	public void testRetainAllList() throws Exception {
		Set<String> set = SortedSets.tree("string1", "string2", "string3")
			.retainAll(SortedSets.of("string2", "string3"))
			.build();
		assertThat(set, contains("string2", "string3"));
	}

	private Predicate<Object> numbers() {
		return new Predicate<Object>() {

			@Override
			public boolean evaluate(Object object) {
				return object instanceof Number;
			}
		};
	}

	private Comparator<Object> reverse() {
		return new Comparator<Object>() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public int compare(Object o1, Object o2) {
				if (o1 instanceof Comparable<?> && o2 instanceof Comparable<?>) {
					return ((Comparable) o2).compareTo((Comparable) o1);
				}
				return 0;
			}
		};
	}
}
