package net.amygdalum.util.text;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.byteArrayContaining;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.copyOfRange;
import static net.amygdalum.util.text.ByteUtils.revert;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ByteStringTest {

    @Test
    public void testGetString() throws Exception {
        assertThat(new ByteString("abc".getBytes(), UTF_8).getString(), equalTo("abc"));
        assertThat(new ByteString("abc".getBytes(UTF_16), UTF_16).getString(), equalTo("abc"));
    }

    @Test
    public void testGetBytes() throws Exception {
        assertThat(new ByteString("abc".getBytes(), UTF_8).getBytes(), byteArrayContaining("abc".getBytes()));
        assertThat(new ByteString("abc".getBytes(UTF_16), UTF_16).getBytes(), byteArrayContaining("abc".getBytes(UTF_16)));
    }

    @Test
    public void testCharset() throws Exception {
        assertThat(new ByteString("abc".getBytes(), UTF_8).charset(), equalTo(UTF_8));
        assertThat(new ByteString("abc".getBytes(UTF_16), UTF_16).charset(), equalTo(UTF_16));
    }

    @Test
    public void testLength() throws Exception {
        assertThat(new ByteString("abc".getBytes(), UTF_8).length(), equalTo(3));
        assertThat(new ByteString("abcd".getBytes(), UTF_8).length(), equalTo(4));
        assertThat(new ByteString("öäü_oau".getBytes(UTF_8), UTF_8).length(), equalTo(10));
        assertThat(new ByteString("abc".getBytes(UTF_16), UTF_16).length(), equalTo(8));
        assertThat(new ByteString("abc".getBytes(UTF_16LE), UTF_16LE).length(), equalTo(6));
    }

    @Test
    public void testEquals() throws Exception {
        assertThat(new ByteString("abc".getBytes(), UTF_8).equals("abc".getBytes()), is(true));
        assertThat(new ByteString("abc".getBytes(UTF_16), UTF_16).equals("abc".getBytes(UTF_16)), is(true));
        assertThat(new ByteString("abc".getBytes(UTF_16), UTF_16).equals("abc".getBytes()), is(false));
    }

    @Test
    public void testRevert() throws Exception {
        assertThat(new ByteString("abc".getBytes(), UTF_8).revert().getBytes(), byteArrayContaining("cba".getBytes()));
        assertThat(new ByteString("abc".getBytes(UTF_16), UTF_16).revert().getBytes(), not(byteArrayContaining("cba".getBytes(UTF_16))));
        assertThat(new ByteString("abc".getBytes(UTF_16), UTF_16).revert().getBytes(), byteArrayContaining(revert("abc".getBytes(UTF_16))));
    }

    @Test
    public void testGetMappablePrefix() throws Exception {
        byte[] abc = "abc".getBytes(UTF_16);
        assertThat(new ByteString(abc, UTF_16).getMappablePrefix(), equalTo("abc"));
        assertThat(new ByteString(copyOfRange(abc, 0, 7), UTF_16).getMappablePrefix(), equalTo("ab"));
        assertThat(new ByteString(copyOfRange(abc, 0, 3), UTF_16).getMappablePrefix(), equalTo(""));
        assertThat(new ByteString(copyOfRange(abc, 0, 1), UTF_16).getMappablePrefix(), equalTo(""));
    }

    @Test
    public void testGetMappableSuffix() throws Exception {
        byte[] abc = "abc".getBytes(UTF_16LE);
        assertThat(new ByteString(abc, UTF_16LE).getMappableSuffix(), equalTo("abc"));
        assertThat(new ByteString(copyOfRange(abc, 1, 6), UTF_16LE).getMappableSuffix(), equalTo("bc"));
        assertThat(new ByteString(copyOfRange(abc, 5, 6), UTF_16LE).getMappableSuffix(), equalTo(""));
    }

    @Test
    public void testIsMappable() throws Exception {
        byte[] abc = "abc".getBytes(UTF_16);
        assertThat(new ByteString(new byte[0], UTF_16).isMappable(), is(true));
        assertThat(new ByteString(abc, UTF_16).isMappable(), is(true));
        assertThat(new ByteString(copyOfRange(abc, 0, 7), UTF_16).isMappable(), is(false));
        assertThat(new ByteString(copyOfRange(abc, 1, 8), UTF_16).isMappable(), is(false));
    }

}
