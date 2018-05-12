package net.amygdalum.util.tries;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.byteArrayContaining;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ByteTrieTerminalNodeTest {

	@Test
	public void testLength() throws Exception {
		assertThat(new ByteTrieTerminalNode<>("attached").length(), equalTo(0));
	}

	@Test
	public void testNextNodeByte() throws Exception {
		assertThat(new ByteTrieTerminalNode<>("attached").nextNode((byte) 'a'), nullValue());
	}

	@Test
	public void testNextNodeByteArray() throws Exception {
		assertThat(new ByteTrieTerminalNode<>("attached").nextNode("a".getBytes(UTF_8)), nullValue());
	}

	@Test
	public void testNextNodeByteArrayInt() throws Exception {
		assertThat(new ByteTrieTerminalNode<>("attached").nextNode("a".getBytes(UTF_8), 1), nullValue());
	}

	@Test
	public void testGetAttached() throws Exception {
		assertThat(new ByteTrieTerminalNode<>("attached").getAttached(), equalTo("attached"));
	}

	@Test
	public void testGetAlternatives() throws Exception {
		assertThat(new ByteTrieTerminalNode<>("attached").getAlternatives(), byteArrayContaining());
	}

	@Test
	public void testLink() throws Exception {
		ByteTrieTerminalNode<String> node = new ByteTrieTerminalNode<String>("chars");
		ByteTrieNode<String> link = new ByteTrieSingleNode<String>("other".getBytes(UTF_8), "other");

		node.link(link);

		assertThat(node.getLink(), equalTo(link));
	}

}
