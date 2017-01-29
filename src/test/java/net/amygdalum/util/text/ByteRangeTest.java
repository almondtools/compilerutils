package net.amygdalum.util.text;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ByteRangeTest {

	@Test
	public void testContainsForOneByte() throws Exception {
		assertThat(new ByteRange(b(0), b(255), 256).contains(b(0)), is(true));
		assertThat(new ByteRange(b(0), b(255), 256).contains(b(127)), is(true));
		assertThat(new ByteRange(b(0), b(255), 256).contains(b(128)), is(true));
		assertThat(new ByteRange(b(0), b(255), 256).contains(b(255)), is(true));

		assertThat(new ByteRange(b(64), b(192), 256).contains(b(0)), is(false));
		assertThat(new ByteRange(b(64), b(192), 256).contains(b(63)), is(false));
		assertThat(new ByteRange(b(64), b(192), 256).contains(b(64)), is(true));
		assertThat(new ByteRange(b(64), b(192), 256).contains(b(127)), is(true));
		assertThat(new ByteRange(b(64), b(192), 256).contains(b(128)), is(true));
		assertThat(new ByteRange(b(64), b(192), 256).contains(b(192)), is(true));
		assertThat(new ByteRange(b(64), b(192), 256).contains(b(193)), is(false));
		assertThat(new ByteRange(b(64), b(192), 256).contains(b(255)), is(false));
	}

	@Test
	public void testContainsForMultipleByte() throws Exception {
		assertThat(new ByteRange(b(0,0), b(255,255), 256*256).contains(b(0,0)), is(true));
		assertThat(new ByteRange(b(0,0), b(255,255), 256*256).contains(b(0,127)), is(true));
		assertThat(new ByteRange(b(0,0), b(255,255), 256*256).contains(b(0,128)), is(true));
		assertThat(new ByteRange(b(0,0), b(255,255), 256*256).contains(b(127,255)), is(true));
		assertThat(new ByteRange(b(0,0), b(255,255), 256*256).contains(b(128,0)), is(true));
		assertThat(new ByteRange(b(0,0), b(255,255), 256*256).contains(b(255,127)), is(true));
		assertThat(new ByteRange(b(0,0), b(255,255), 256*256).contains(b(255,128)), is(true));
		assertThat(new ByteRange(b(0,0), b(255,255), 256*256).contains(b(255,255)), is(true));
		
		assertThat(new ByteRange(b(64,64), b(192,192), 256).contains(b(0,0)), is(false));
		assertThat(new ByteRange(b(64,64), b(192,192), 256).contains(b(63,255)), is(false));
		assertThat(new ByteRange(b(64,64), b(192,192), 256).contains(b(64,63)), is(false));
		assertThat(new ByteRange(b(64,64), b(192,192), 256).contains(b(64,64)), is(true));
		assertThat(new ByteRange(b(64,64), b(192,192), 256).contains(b(128,128)), is(true));
		assertThat(new ByteRange(b(64,64), b(192,192), 256).contains(b(192,192)), is(true));
		assertThat(new ByteRange(b(64,64), b(192,192), 256).contains(b(192,193)), is(false));
		assertThat(new ByteRange(b(64,64), b(192,192), 256).contains(b(193,0)), is(false));
		assertThat(new ByteRange(b(64,64), b(192,192), 256).contains(b(255,255)), is(false));
	}

	private byte b(int i) {
		return (byte) i;
	}

	private byte[] b(int... i) {
		byte[] bs = new byte[i.length];
		for (int j = 0; j < bs.length; j++) {
			bs[j] = (byte) i[j];
		}
		return bs;
	}
}
