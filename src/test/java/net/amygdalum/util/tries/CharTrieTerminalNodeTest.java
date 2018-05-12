package net.amygdalum.util.tries;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.charArrayContaining;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CharTrieTerminalNodeTest {

	@Test
	public void testLength() throws Exception {
		assertThat(new CharTrieTerminalNode<>("attached").length(), equalTo(0));
	}

	@Test
	public void testNextNodeByte() throws Exception {
		assertThat(new CharTrieTerminalNode<>("attached").nextNode('a'), nullValue());
	}

	@Test
	public void testNextNodeByteArray() throws Exception {
		assertThat(new CharTrieTerminalNode<>("attached").nextNode("a".toCharArray()), nullValue());
	}

	@Test
	public void testNextNodeByteArrayInt() throws Exception {
		assertThat(new CharTrieTerminalNode<>("attached").nextNode("a".toCharArray(), 1), nullValue());
	}

	@Test
	public void testGetAttached() throws Exception {
		assertThat(new CharTrieTerminalNode<>("attached").getAttached(), equalTo("attached"));
	}

	@Test
	public void testGetAlternatives() throws Exception {
		assertThat(new CharTrieTerminalNode<>("attached").getAlternatives(), charArrayContaining());
	}

	@Test
	public void testLink() throws Exception {
		CharTrieTerminalNode<String> node = new CharTrieTerminalNode<String>("chars");
		CharTrieNode<String> link = new CharTrieSingleNode<String>("other".toCharArray(), "other");

		node.link(link);

		assertThat(node.getLink(), equalTo(link));
	}

}
