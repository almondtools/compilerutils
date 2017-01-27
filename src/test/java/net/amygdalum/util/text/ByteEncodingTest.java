package net.amygdalum.util.text;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.byteArrayContaining;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.amygdalum.util.text.ByteEncoding.encode;
import static net.amygdalum.util.text.ByteEncoding.intervals;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ByteEncodingTest {

	@Test
	public void testEncodeInUTF8withASCIIString() throws Exception {
		assertThat(encode("a"), byteArrayContaining((byte) 'a'));
		assertThat(encode("ab"), byteArrayContaining((byte) 'a', (byte) 'b'));
	}

	@Test
	public void testEncodeCharInUTF8withASCIIString() throws Exception {
		assertThat(encode('a'), byteArrayContaining((byte) 'a'));
		assertThat(encode('z'), byteArrayContaining((byte) 'z'));
	}

	@Test
	public void testEncodeInUTF8withNonASCIIString() throws Exception {
		assertThat(encode("ö"), byteArrayContaining((byte) 0xC3, (byte) 0xB6));
		assertThat(encode("ß=sz"), byteArrayContaining((byte) 0xC3, (byte) 0x9F, (byte) '=', (byte) 's', (byte) 'z'));
	}

	@Test
	public void testEncodeCharInUTF8withNonASCIIString() throws Exception {
		assertThat(encode('ö'), byteArrayContaining((byte) 0xC3, (byte) 0xB6));
		assertThat(encode('ß'), byteArrayContaining((byte) 0xC3, (byte) 0x9F));
	}

	@Test
	public void testIntervalsInASCIIRange() throws Exception {
		assertThat(intervals(UTF_8, 'a', 'z'), contains(new ByteRange((byte)'a',(byte)'z', (int) ('z'-'a'))));
	}

	@Test
	public void testIntervalsIn2ByteRange() throws Exception {
		assertThat(intervals(UTF_8, 'a', 'ö'), contains(
			new ByteRange((byte)'a',(byte) 0x7f, (int) (0x80-'a')), 
			new ByteRange(new byte[]{(byte)0xc2, (byte) 0x80},new byte[]{(byte)0xc3, (byte) 0xb6}, (int) ('ö' - 0x80))));
	}

	@Test
	public void testIntervalsIn3ByteRange() throws Exception {
		assertThat(intervals(UTF_8, 'a', (char) 0x2afc), contains(
			new ByteRange((byte)'a',(byte) 0x7f, (int) (0x80-'a')), 
			new ByteRange(new byte[]{(byte)0xc2, (byte) 0x80},new byte[]{(byte)0xdf, (byte) 0xbf}, (int) (0x0800 - 0x80)),
			new ByteRange(new byte[]{(byte)0xe0, (byte) 0xa0, (byte) 0x80},new byte[]{(byte)0xe2, (byte) 0xab, (byte) 0xbc}, (int) (0x2afc - 0x0800))
			));
	}

}
