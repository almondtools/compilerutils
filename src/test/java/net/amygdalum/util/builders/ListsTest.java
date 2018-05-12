package net.amygdalum.util.builders;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class ListsTest {

	@Test
	public void testFactories() throws Exception {
		assertThat(Lists.empty().build(), empty());
		assertThat(Lists.list("string").build(), contains("string"));
		assertThat(Lists.of("string"), contains("string"));
		assertThat(Lists.ofLinked("string"), contains("string"));
		assertThat(Lists.of(numbers(), "string", 42), contains((Object) 42));
		assertThat(Lists.ofLinked(numbers(), "string", 42), contains((Object) 42));
		assertThat(Lists.ofPrimitives(new int[] { 21, 42 }), contains(21, 42));
		assertThat(Lists.ofLinkedPrimitives(new int[] { 21, 42 }), contains(21, 42));
	}

	@Test
	public void testAdd() throws Exception {
		List<String> list = Lists.<String> empty()
			.add("string")
			.build();
		assertThat(list, contains("string"));
	}

	@Test
	public void testAddConditional() throws Exception {
		List<String> list = Lists.<String> empty()
			.addConditional(true, "string included")
			.addConditional(false, "string excluded")
			.build();
		assertThat(list, contains("string included"));
	}

	@Test
	public void testAddAllArray() throws Exception {
		List<String> list = Lists.<String> empty()
			.addAll("string1", "string2")
			.build();
		assertThat(list, contains("string1", "string2"));
	}

	@Test
	public void testAddAllList() throws Exception {
		List<String> list = Lists.<String> empty()
			.addAll(Lists.of("string2", "string3"))
			.build();
		assertThat(list, contains("string2", "string3"));
	}

	@Test
	public void testRemove() throws Exception {
		List<String> list = Lists.list("string to remove", "string")
			.remove("string to remove")
			.build();
		assertThat(list, contains("string"));
	}

	@Test
	public void testRemoveConditional() throws Exception {
		List<String> list = Lists.list("string included", "string excluded")
			.removeConditional(false, "string included")
			.removeConditional(true, "string excluded")
			.build();
		assertThat(list, contains("string included"));
	}

	@Test
	public void testRemoveAllArray() throws Exception {
		List<String> list = Lists.list("string1", "string2","string3")
			.removeAll("string1","string3")
			.build();
		assertThat(list, contains("string2"));
	}

	@Test
	public void testRemoveAllList() throws Exception {
		List<String> list = Lists.list("string1", "string2","string3")
			.removeAll(Lists.of("string2","string3"))
			.build();
		assertThat(list, contains("string1"));
	}

	@Test
	public void testRetain() throws Exception {
		List<String> list = Lists.list("string to retain", "string")
			.retain("string to retain")
			.build();
		assertThat(list, contains("string to retain"));
	}

	@Test
	public void testRetainConditional() throws Exception {
		List<String> list = Lists.list("string to retain", "string")
			.retainConditional(false, "string")
			.retainConditional(true, "string to retain")
			.build();
		assertThat(list, contains("string to retain"));
	}

	@Test
	public void testRetainAllArray() throws Exception {
		List<String> list = Lists.list("string1", "string2","string3")
			.retainAll("string1","string3")
			.build();
		assertThat(list, contains("string1","string3"));
	}

	@Test
	public void testRetainAllList() throws Exception {
		List<String> list = Lists.list("string1", "string2","string3")
			.retainAll(Lists.of("string2","string3"))
			.build();
		assertThat(list, contains("string2","string3"));
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
