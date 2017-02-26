package net.amygdalum.util.io;

import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.junit.Test;

public class StringByteProviderTest {

	@Test
	public void testRestart() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);
		provider.restart();
		assertThat(provider.current(), equalTo(0l));
	}

	@Test
	public void testNextAtBeginning() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 0);
		assertThat(provider.next(), equalTo((byte) 'a'));
	}

	@Test
	public void testNextAtOddIndex() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 1);
		assertThat(provider.next(), equalTo((byte) ('a' >> 8)));
	}

	@Test
	public void testNextInMiddle() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);
		assertThat(provider.next(), equalTo((byte) 'b'));
	}

	@Test
	public void testNextConsumes() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 0);
		assertThat(provider.next(), equalTo((byte) 'a'));
		assertThat(provider.next(), equalTo((byte) ('a' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'b'));
		assertThat(provider.next(), equalTo((byte) ('b' >> 8)));
	}

	@Test
	public void testPrevInMiddle() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);
		assertThat(provider.prev(), equalTo((byte) ('a' >> 8)));
	}

	@Test
	public void testPrevAtEnd() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 8);
		assertThat(provider.prev(), equalTo((byte) ('d' >> 8)));
	}

	@Test
	public void testPrevAtOddIndex() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 7);
		assertThat(provider.prev(), equalTo((byte) 'd'));
	}

	@Test
	public void testPrevConsumes() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 8);
		assertThat(provider.prev(), equalTo((byte) ('d' >> 8)));
		assertThat(provider.prev(), equalTo((byte) 'd'));
		assertThat(provider.prev(), equalTo((byte) ('c' >> 8)));
		assertThat(provider.prev(), equalTo((byte) 'c'));
	}

	@Test
	public void testFinishedAtBeginning() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 0);
		assertThat(provider.finished(), is(false));
	}

	@Test
	public void testFinishedAtOddIndex() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 1);
		assertThat(provider.finished(), is(false));
	}

	@Test
	public void testFinishedInMiddle() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);
		assertThat(provider.finished(), is(false));
	}

	@Test
	public void testFinishedAtEnd() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 8);
		assertThat(provider.finished(), is(true));
	}

	@Test
	public void testLookahead() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 0);
		assertThat(provider.lookahead(), equalTo((byte) 'a'));
	}

	@Test
	public void testLookaheadWithN() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 0);
		assertThat(provider.lookahead(0), equalTo((byte) 'a'));
		assertThat(provider.lookahead(1), equalTo((byte) ('a' >> 8)));
		assertThat(provider.lookahead(2), equalTo((byte) 'b'));
		assertThat(provider.lookahead(3), equalTo((byte) ('b' >> 8)));
		assertThat(provider.lookahead(4), equalTo((byte) 'c'));
		assertThat(provider.lookahead(5), equalTo((byte) ('c' >> 8)));
	}

	@Test
	public void testLookbehind() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 8);
		assertThat(provider.lookbehind(), equalTo((byte) ('d' >> 8)));
	}

	@Test
	public void testLookbehindWithN() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 8);
		assertThat(provider.lookbehind(0), equalTo((byte) ('d' >> 8)));
		assertThat(provider.lookbehind(1), equalTo((byte) 'd'));
		assertThat(provider.lookbehind(2), equalTo((byte) ('c' >> 8)));
		assertThat(provider.lookbehind(3), equalTo((byte) 'c'));
		assertThat(provider.lookbehind(4), equalTo((byte) ('b' >> 8)));
		assertThat(provider.lookbehind(5), equalTo((byte) 'b'));
	}

	@Test
	public void testCurrent() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);
		assertThat(provider.current(), equalTo(2l));
	}

	@Test
	public void testMoveAndCurrent() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);
		provider.move(3);
		assertThat(provider.current(), equalTo(3l));
	}

	@Test
	public void testAtDoesNotConsume() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 0);
		assertThat(provider.at(4), equalTo((byte) 'c'));
		assertThat(provider.current(), equalTo(0l));
	}

	@Test
	public void testSliceDoesNotConsume() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 0);
		assertThat(provider.slice(2, 6).getString(), equalTo("bc"));
		assertThat(provider.current(), equalTo(0l));
	}

	@Test
	public void testBetweenDoesNotConsume() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 0);
		assertThat(provider.between(2, 6), equalTo(new byte[] { 'b', 'b' >> 8, 'c', 'c' >> 8 }));
		assertThat(provider.current(), equalTo(0l));
	}

	@Test
	public void testToStringAtBeginning() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 0);
		assertThat(provider.toString(), equalTo("|abcd"));
	}

	@Test
	public void testToStringInMiddle() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);
		assertThat(provider.toString(), equalTo("a|bcd"));
	}

	@Test
	public void testToStringAtOddIndex() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 3);
		assertThat(provider.toString(), equalTo("a~|~cd"));
	}

	@Test
	public void testToStringAtEnd() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 8);
		assertThat(provider.toString(), equalTo("abcd|"));
	}

	@Test
	public void testChangedWithoutChange() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);

		assertThat(provider.changed(), is(false));
	}

	@Test
	public void testChangedWithChange() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);

		provider.mark();

		provider.forward(1);

		assertThat(provider.changed(), is(true));
	}

	@Test
	public void testChangedWithTemporaryChange() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);

		provider.mark();

		provider.next();
		provider.prev();

		assertThat(provider.changed(), is(false));
	}

	@Test
	public void testChangedDoubleCall() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);

		provider.mark();

		provider.forward(1);
		provider.changed();

		assertThat(provider.changed(), is(false));
	}

	@Test
	public void testFinish() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 2);

		provider.finish();

		assertThat(provider.finished(), is(true));
	}

	@Test
	public void testFinished() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 6);

		assertThat(provider.finished(1), is(false));
		assertThat(provider.finished(2), is(true));
	}

	@Test
	public void testFinishedNotChangesState() throws Exception {
		StringByteProvider provider = new StringByteProvider("abcd", 6);

		assertThat(provider.finished(2), is(true));
		assertThat(provider.finished(1), is(false));
	}

	@Test
	public void testFinishedWithEncodedBytes() throws Exception {
		StringByteProvider provider = new StringByteProvider("a", 0, UTF_8);

		provider.next();

		assertThat(provider.finished(), is(true));
	}

	@Test
	public void testBytesWithUTF8() throws Exception {
		ByteProvider provider = new StringByteProvider("abcößyéà".getBytes(UTF_8), 0, UTF_8);
		assertThat(provider.next(), equalTo((byte) 'a'));
		assertThat(provider.next(), equalTo((byte) 'b'));
		assertThat(provider.next(), equalTo((byte) 'c'));
		assertThat(provider.next(), equalTo((byte) encode('ö', UTF_8, 0)));
		assertThat(provider.next(), equalTo((byte) encode('ö', UTF_8, 1)));
		assertThat(provider.next(), equalTo((byte) encode('ß', UTF_8, 0)));
		assertThat(provider.next(), equalTo((byte) encode('ß', UTF_8, 1)));
		assertThat(provider.next(), equalTo((byte) 'y'));
		assertThat(provider.next(), equalTo((byte) encode('é', UTF_8, 0)));
		assertThat(provider.next(), equalTo((byte) encode('é', UTF_8, 1)));
		assertThat(provider.next(), equalTo((byte) encode('à', UTF_8, 0)));
		assertThat(provider.next(), equalTo((byte) encode('à', UTF_8, 1)));
	}

	@Test
	public void testBytesWithUTF16() throws Exception {
		ByteProvider provider = new StringByteProvider("abcößyéà".getBytes(UTF_16LE), 0, UTF_16LE);
		assertThat(provider.next(), equalTo((byte) 'a'));
		assertThat(provider.next(), equalTo((byte) ('a' >>> 8)));
		assertThat(provider.next(), equalTo((byte) 'b'));
		assertThat(provider.next(), equalTo((byte) ('b' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'c'));
		assertThat(provider.next(), equalTo((byte) ('c' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'ö'));
		assertThat(provider.next(), equalTo((byte) ('ö' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'ß'));
		assertThat(provider.next(), equalTo((byte) ('ß' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'y'));
		assertThat(provider.next(), equalTo((byte) ('y' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'é'));
		assertThat(provider.next(), equalTo((byte) ('é' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'à'));
		assertThat(provider.next(), equalTo((byte) ('à' >> 8)));
	}

	@Test
	public void testStringWithUTF8() throws Exception {
		ByteProvider provider = new StringByteProvider("abcößyéà", 0, UTF_8);
		assertThat(provider.next(), equalTo((byte) 'a'));
		assertThat(provider.next(), equalTo((byte) 'b'));
		assertThat(provider.next(), equalTo((byte) 'c'));
		assertThat(provider.next(), equalTo((byte) encode('ö', UTF_8, 0)));
		assertThat(provider.next(), equalTo((byte) encode('ö', UTF_8, 1)));
		assertThat(provider.next(), equalTo((byte) encode('ß', UTF_8, 0)));
		assertThat(provider.next(), equalTo((byte) encode('ß', UTF_8, 1)));
		assertThat(provider.next(), equalTo((byte) 'y'));
		assertThat(provider.next(), equalTo((byte) encode('é', UTF_8, 0)));
		assertThat(provider.next(), equalTo((byte) encode('é', UTF_8, 1)));
		assertThat(provider.next(), equalTo((byte) encode('à', UTF_8, 0)));
		assertThat(provider.next(), equalTo((byte) encode('à', UTF_8, 1)));
	}

	@Test
	public void testStringWithUTF16() throws Exception {
		ByteProvider provider = new StringByteProvider("abcößyéà", 0, UTF_16LE);
		assertThat(provider.next(), equalTo((byte) 'a'));
		assertThat(provider.next(), equalTo((byte) ('a' >>> 8)));
		assertThat(provider.next(), equalTo((byte) 'b'));
		assertThat(provider.next(), equalTo((byte) ('b' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'c'));
		assertThat(provider.next(), equalTo((byte) ('c' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'ö'));
		assertThat(provider.next(), equalTo((byte) ('ö' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'ß'));
		assertThat(provider.next(), equalTo((byte) ('ß' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'y'));
		assertThat(provider.next(), equalTo((byte) ('y' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'é'));
		assertThat(provider.next(), equalTo((byte) ('é' >> 8)));
		assertThat(provider.next(), equalTo((byte) 'à'));
		assertThat(provider.next(), equalTo((byte) ('à' >> 8)));
	}

	private static byte encode(char c, Charset charset, int i) {
		ByteBuffer encoded = charset.encode(CharBuffer.wrap(new char[]{c}));
		return encoded.get(i);
	}

}
