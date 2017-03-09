package net.amygdalum.util.map;

import static net.amygdalum.util.map.TuneableMap.mask;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TuneableMapTest {

	@Test
	public void testMaskOnEmpty() throws Exception {
		assertThat(mask(0, 0.75f), equalTo(1));
	}

	@Test
	public void testMaskOnSmall() throws Exception {
		assertThat(mask(1, 0.75f), equalTo(1));
	}

	@Test
	public void testMaskOnSmallLoadfactor() throws Exception {
		assertThat(mask(1, 0.4f), equalTo(3));
	}

	@Test
	public void testMaskOnLargerSize() throws Exception {
		assertThat(mask(2, 0.75f), equalTo(3));
	}

}
