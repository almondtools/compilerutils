package net.amygdalum.util.builders;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.booleanArrayContaining;
import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.byteArrayContaining;
import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.charArrayContaining;
import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.doubleArrayContaining;
import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.floatArrayContaining;
import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.intArrayContaining;
import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.longArrayContaining;
import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.shortArrayContaining;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PrimitiveArraysTest {

	@Test
	public void testRevertChar() throws Exception {
		char[] array = new char[] { '1', '2' };

		PrimitiveArrays.revert(array);

		assertThat(array, charArrayContaining('2','1'));
	}

	@Test
	public void testRevertBoolean() throws Exception {
		boolean[] array = new boolean[] { true, false };
		
		PrimitiveArrays.revert(array);
		
		assertThat(array, booleanArrayContaining(false, true));
	}
	
	@Test
	public void testRevertByte() throws Exception {
		byte[] array = new byte[] { 1, 2 };
		
		PrimitiveArrays.revert(array);
		
		assertThat(array, byteArrayContaining((byte) 2, (byte) 1));
	}
	
	@Test
	public void testRevertShort() throws Exception {
		short[] array = new short[] { 1, 2 };

		PrimitiveArrays.revert(array);

		assertThat(array, shortArrayContaining((short) 2, (short) 1));
	}

	@Test
	public void testRevertInt() throws Exception {
		int[] array = new int[] { 1, 2 };

		PrimitiveArrays.revert(array);

		assertThat(array, intArrayContaining(2, 1));
	}

	@Test
	public void testRevertLong() throws Exception {
		long[] array = new long[] { 1, 2 };

		PrimitiveArrays.revert(array);

		assertThat(array, longArrayContaining(2l, 1l));
	}

	@Test
	public void testRevertFloat() throws Exception {
		float[] array = new float[] { 1.0f, 2.0f };

		PrimitiveArrays.revert(array);

		assertThat(array, floatArrayContaining(2.0f, 1.0f));
	}

	@Test
	public void testRevertDouble() throws Exception {
		double[] array = new double[] { 1.0d, 2.0d };

		PrimitiveArrays.revert(array);

		assertThat(array, doubleArrayContaining(2.0d, 1.0d));
	}

}
