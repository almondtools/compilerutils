package net.amygdalum.util.text;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static net.amygdalum.util.text.ByteUtils.after;
import static net.amygdalum.util.text.ByteUtils.before;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
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
		assertThat(new ByteRange(b(0, 0), b(255, 255), 256 * 256).contains(b(0, 0)), is(true));
		assertThat(new ByteRange(b(0, 0), b(255, 255), 256 * 256).contains(b(0, 127)), is(true));
		assertThat(new ByteRange(b(0, 0), b(255, 255), 256 * 256).contains(b(0, 128)), is(true));
		assertThat(new ByteRange(b(0, 0), b(255, 255), 256 * 256).contains(b(127, 255)), is(true));
		assertThat(new ByteRange(b(0, 0), b(255, 255), 256 * 256).contains(b(128, 0)), is(true));
		assertThat(new ByteRange(b(0, 0), b(255, 255), 256 * 256).contains(b(255, 127)), is(true));
		assertThat(new ByteRange(b(0, 0), b(255, 255), 256 * 256).contains(b(255, 128)), is(true));
		assertThat(new ByteRange(b(0, 0), b(255, 255), 256 * 256).contains(b(255, 255)), is(true));

		assertThat(new ByteRange(b(64, 64), b(192, 192), 256).contains(b(0, 0)), is(false));
		assertThat(new ByteRange(b(64, 64), b(192, 192), 256).contains(b(63, 255)), is(false));
		assertThat(new ByteRange(b(64, 64), b(192, 192), 256).contains(b(64, 63)), is(false));
		assertThat(new ByteRange(b(64, 64), b(192, 192), 256).contains(b(64, 64)), is(true));
		assertThat(new ByteRange(b(64, 64), b(192, 192), 256).contains(b(128, 128)), is(true));
		assertThat(new ByteRange(b(64, 64), b(192, 192), 256).contains(b(192, 192)), is(true));
		assertThat(new ByteRange(b(64, 64), b(192, 192), 256).contains(b(192, 193)), is(false));
		assertThat(new ByteRange(b(64, 64), b(192, 192), 256).contains(b(193, 0)), is(false));
		assertThat(new ByteRange(b(64, 64), b(192, 192), 256).contains(b(255, 255)), is(false));
	}

	@Test
	public void testByteRange() throws Exception {
		assertThat(new ByteRange(b('q'), b('t')), satisfiesDefaultEquality()
			.andEqualTo(new ByteRange(b('q'), b('t')))
			.andNotEqualTo(new ByteRange(b('q'), b('s')))
			.andNotEqualTo(new ByteRange(b('p'), b('t')))
			.andNotEqualTo(new ByteRange(b('q'), b('t'), 4))
			.includingToString());
	}

	@Test
	public void testByteRangeArray() throws Exception {
		assertThat(new ByteRange(b(1, 0), b(2, 0)), satisfiesDefaultEquality()
			.andEqualTo(new ByteRange(b(1, 0), b(2, 0)))
			.andNotEqualTo(new ByteRange(b(1, 0), b(2, 1)))
			.andNotEqualTo(new ByteRange(b(1, 1), b(2, 0)))
			.andNotEqualTo(new ByteRange(b(1, 0), b(2, 0), 4))
			.includingToString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testByteRangeIllegal() throws Exception {
		new ByteRange(b(1, 0), b(2, 0,0));
	}



	@Test
	public void testContains() throws Exception {
		assertThat(new ByteRange(b('a'), b('c')).contains(before(b('a'))), is(false));
		assertThat(new ByteRange(b('a'), b('c')).contains(b('a')), is(true));
		assertThat(new ByteRange(b('a'), b('c')).contains(b('b')), is(true));
		assertThat(new ByteRange(b('a'), b('c')).contains(b('c')), is(true));
		assertThat(new ByteRange(b('a'), b('c')).contains(after(b('c'))), is(false));
	}

	@Test
	public void testContainsWithDifferentLengths() throws Exception {
		assertThat(new ByteRange(b(1), b(2)).contains(b(0, 1)), is(false));
		assertThat(new ByteRange(b(0, 0), b(1, 1)).contains(b(1)), is(false));
	}

	@Test
	public void testSplitBefore() throws Exception {
		assertThat(new ByteRange(b('q'), b('t')).splitBefore(b(0, 's')), contains(new ByteRange(b('q'), b('t'))));
		assertThat(new ByteRange(b('q'), b('t')).splitBefore(b('s')), contains(new ByteRange(b('q'), b('r')), new ByteRange(b('s'), b('t'))));
		assertThat(new ByteRange(b('q'), b('t')).splitBefore(b('q')), contains(new ByteRange(b('q'), b('t'))));
	}

	@Test
	public void testSplitAfter() throws Exception {
		assertThat(new ByteRange(b('q'), b('t')).splitAfter(b(0, 's')), contains(new ByteRange(b('q'), b('t'))));
		assertThat(new ByteRange(b('q'), b('t')).splitAfter(b('s')), contains(new ByteRange(b('q'), b('s')), new ByteRange(b('t'), b('t'))));
		assertThat(new ByteRange(b('q'), b('t')).splitAfter(b('t')), contains(new ByteRange(b('q'), b('t'))));
	}

	@Test
	public void testSplitAround() throws Exception {
		assertThat(new ByteRange(b('q'), b('t')).splitAround(b('r'), b('s')), contains(new ByteRange(b('q'), b('q')), new ByteRange(b('r'), b('s')), new ByteRange(b('t'), b('t'))));
		assertThat(new ByteRange(b('q'), b('t')).splitAround(b('q'), b('s')), contains(new ByteRange(b('q'), b('s')), new ByteRange(b('t'), b('t'))));
		assertThat(new ByteRange(b('q'), b('t')).splitAround(b('r'), b('t')), contains(new ByteRange(b('q'), b('q')), new ByteRange(b('r'), b('t'))));
		assertThat(new ByteRange(b('q'), b('t')).splitAround(b('q'), b('t')), contains(new ByteRange(b('q'), b('t'))));

		assertThat(new ByteRange(b(0, b('q')), b(0, b('t'))).splitAround(b(0, b('r')), b(0, b('s'))), contains(
			new ByteRange(b(0, 'q'), b(0, 'q')),
			new ByteRange(b(0, b('r')), b(0, b('s'))),
			new ByteRange(b(0, b('t')), b(0, b('t')))));
		assertThat(new ByteRange(b(0, b('q')), b(0, b('t'))).splitAround(b(0, b('q')), b(0, b('s'))), contains(
			new ByteRange(b(0, 'q'), b(0, 's')),
			new ByteRange(b(0, b('t')), b(0, b('t')))));
		assertThat(new ByteRange(b(0, b('q')), b(0, b('t'))).splitAround(b(0, b('r')), b(0, b('t'))), contains(
			new ByteRange(b(0, 'q'), b(0, 'q')),
			new ByteRange(b(0, b('r')), b(0, b('t')))));
		assertThat(new ByteRange(b(0, b('q')), b(0, b('t'))).splitAround(b(0, b('q')), b(0, b('t'))), contains(
			new ByteRange(b(0, 'q'), b(0, 't'))));

		assertThat(new ByteRange(b(0, b('q')), b(0, b('t'))).splitAround(b('r'), b('s')), contains(
			new ByteRange(b(0, b('q')), b(0, b('t')))));
		assertThat(new ByteRange(b(0, b('q')), b(0, b('t'))).splitAround(b(0, 0, b('r')), b(0, b('s'))), contains(
			new ByteRange(b(0, b('q')), b(0, b('t')))));
		assertThat(new ByteRange(b(0, b('q')), b(0, b('t'))).splitAround(b(0, b('r')), b(0, 0, b('s'))), contains(
			new ByteRange(b(0, b('q')), b(0, b('t')))));
		assertThat(new ByteRange(b('q'), b('t')).splitAround(b(0, b('r')), b(0, b('s'))), contains(
			new ByteRange(b('q'), b('t'))));
	}

	@Test
	public void testSplitArray() throws Exception {
		assertThat(new ByteRange(b(0,0), b(255,255)).splitAfter(b(0, 42)), contains(
			new ByteRange(b(0,0), b(0,42)),
			new ByteRange(b(0,43), b(255,255))));
		assertThat(new ByteRange(b(0,0), b(255,255)).splitAfter(b(0, 255)), contains(
			new ByteRange(b(0,0), b(0,255)),
			new ByteRange(b(1,0), b(255,255))));
		assertThat(new ByteRange(b(0,0), b(255,255)).splitAfter(b(255, 255)), contains(
			new ByteRange(b(0,0), b(255,255))));
		assertThat(new ByteRange(b(0,0), b(255,255)).splitBefore(b(0, 42)), contains(
			new ByteRange(b(0,0), b(0,41)),
			new ByteRange(b(0,42), b(255,255))));
		assertThat(new ByteRange(b(0,0), b(255,255)).splitBefore(b(1, 0)), contains(
			new ByteRange(b(0,0), b(0,255)),
			new ByteRange(b(1,0), b(255,255))));
		assertThat(new ByteRange(b(0,0), b(255,255)).splitBefore(b(0, 0)), contains(
			new ByteRange(b(0,0), b(255,255))));
	}

	@Test
	public void testRangesByteByte() throws Exception {
		assertThat(new ByteRange(b(1), b(2)).ranges(b(1), b(2)), is(true));
		assertThat(new ByteRange(b(1), b(2)).ranges(b(1), b(3)), is(false));
		assertThat(new ByteRange(b(1), b(2)).ranges(b(2), b(2)), is(false));

		assertThat(new ByteRange(b(0, 1), b(0, 2)).ranges(b(0, 1), b(0, 2)), is(true));
		assertThat(new ByteRange(b(0, 1), b(0, 2)).ranges(b(0, 0, 1), b(0, 2)), is(false));
		assertThat(new ByteRange(b(0, 1), b(0, 2)).ranges(b(0, 1), b(0, 0, 2)), is(false));
		assertThat(new ByteRange(b(0, 1), b(0, 2)).ranges(b(1), b(2)), is(false));
		assertThat(new ByteRange(b(0, 1), b(0, 2)).ranges(b(1, 1), b(0, 2)), is(false));
		assertThat(new ByteRange(b(0, 1), b(0, 2)).ranges(b(0, 1), b(0, 3)), is(false));
	}

	@Test
	public void testSize() throws Exception {
		assertThat(new ByteRange(b(1), b(2)).size(), equalTo(-1));
		assertThat(new ByteRange(b(1), b(2), 2).size(), equalTo(2));
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
