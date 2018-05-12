package net.amygdalum.util.builders;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;

public class SetsTest {

	@Test
	public void testFactories() throws Exception {
		assertThat(Sets.hashed().build(), empty());
		assertThat(Sets.linked().build(), empty());
		assertThat(Sets.hashed("string").build(), contains("string"));
		assertThat(Sets.linked("string").build(), contains("string"));
		assertThat(Sets.hashed(asList("string")).build(), contains("string"));
		assertThat(Sets.linked(asList("string")).build(), contains("string"));
		assertThat(Sets.of("string"), contains("string"));
		assertThat(Sets.ofLinked("string"), contains("string"));
		assertThat(Sets.of(numbers(), "string", 42), contains((Object) 42));
		assertThat(Sets.ofLinked(numbers(), "string", 42), contains((Object) 42));
		assertThat(Sets.ofPrimitives(new int[] { 21, 42 }), containsInAnyOrder(21, 42));
		assertThat(Sets.ofLinkedPrimitives(new int[] { 21, 42 }), containsInAnyOrder(21, 42));
	}

	@Test
	public void testIntersection() throws Exception {
		Set<String> intersection = Sets.intersectionOf(Sets.of("string in intersection", "string1"), Sets.of("string2", "string in intersection"));
		assertThat(intersection, contains("string in intersection"));
	}

	@Test
	public void testComplement() throws Exception {
		Set<String> s1 = Sets.complementOf(Sets.of("string in intersection", "string1"), Sets.of("string2", "string in intersection"));
		Set<String> s2 = Sets.complementOf(Sets.of("string2", "string in intersection"), Sets.of("string in intersection", "string1"));
		assertThat(s1, contains("string1"));
		assertThat(s2, contains("string2"));
	}

	@Test
	public void testUnion() throws Exception {
		Set<String> union = Sets.unionOf(Sets.of("string in intersection", "string1"), Sets.of("string2", "string in intersection"));
		assertThat(union, containsInAnyOrder("string1", "string2", "string in intersection"));
	}

	@Test
	public void testAdd() throws Exception {
		Set<String> set = Sets.<String> hashed()
			.add("string")
			.build();
		assertThat(set, contains("string"));
	}

	@Test
	public void testAddConditional() throws Exception {
		Set<String> set = Sets.<String> hashed()
			.addConditional(true, "string included")
			.addConditional(false, "string excluded")
			.build();
		assertThat(set, contains("string included"));
	}

	@Test
	public void testAddAllArray() throws Exception {
		Set<String> set = Sets.<String> hashed()
			.addAll("string1", "string2")
			.build();
		assertThat(set, containsInAnyOrder("string1", "string2"));
	}

	@Test
	public void testAddAllList() throws Exception {
		Set<String> set = Sets.<String> hashed()
			.addAll(Sets.of("string2", "string3"))
			.build();
		assertThat(set, containsInAnyOrder("string2", "string3"));
	}

	@Test
	public void testRemove() throws Exception {
		Set<String> set = Sets.hashed("string to remove", "string")
			.remove("string to remove")
			.build();
		assertThat(set, contains("string"));
	}

	@Test
	public void testRemoveConditional() throws Exception {
		Set<String> set = Sets.hashed("string included", "string excluded")
			.removeConditional(false, "string included")
			.removeConditional(true, "string excluded")
			.build();
		assertThat(set, contains("string included"));
	}

	@Test
	public void testRemoveAllArray() throws Exception {
		Set<String> set = Sets.hashed("string1", "string2", "string3")
			.removeAll("string1", "string3")
			.build();
		assertThat(set, contains("string2"));
	}

	@Test
	public void testRemoveAllList() throws Exception {
		Set<String> set = Sets.hashed("string1", "string2", "string3")
			.removeAll(Sets.of("string2", "string3"))
			.build();
		assertThat(set, contains("string1"));
	}

	@Test
	public void testRetain() throws Exception {
		Set<String> set = Sets.hashed("string to retain", "string")
			.retain("string to retain")
			.build();
		assertThat(set, contains("string to retain"));
	}

	@Test
	public void testRetainConditional() throws Exception {
		Set<String> set = Sets.hashed("string to retain", "string")
			.retainConditional(false, "string")
			.retainConditional(true, "string to retain")
			.build();
		assertThat(set, contains("string to retain"));
	}

	@Test
	public void testRetainAllArray() throws Exception {
		Set<String> set = Sets.hashed("string1", "string2", "string3")
			.retainAll("string1", "string3")
			.build();
		assertThat(set, containsInAnyOrder("string1", "string3"));
	}

	@Test
	public void testRetainAllList() throws Exception {
		Set<String> set = Sets.hashed("string1", "string2", "string3")
			.retainAll(Sets.of("string2", "string3"))
			.build();
		assertThat(set, containsInAnyOrder("string2", "string3"));
	}

	private Predicate<Object> numbers() {
		return new Predicate<Object>() {

			@Override
			public boolean evaluate(Object object) {
				return object instanceof Number;
			}
		};
	}

}
