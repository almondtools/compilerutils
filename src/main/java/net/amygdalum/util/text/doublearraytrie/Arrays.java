package net.amygdalum.util.text.doublearraytrie;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.copyOfRange;

public final class Arrays {

	public static final byte[] NO_BYTES = new byte[0];
	public static final char[] NO_CHARS = new char[0];
	
	private Arrays() {
	}
	
	public static int[] expand(int[] array, int next) {
		int newlength = array.length;
		while (newlength < next + 1) {
			newlength *= 2;
		}
		int[] expandedArray = new int[newlength];
		System.arraycopy(array, 0, expandedArray, 0, array.length);
		return expandedArray;
	}

	public static <S> S[] expand(S[] array, int next) {
		int newlength = array.length;
		while (newlength < next + 1) {
			newlength *= 2;
		}
		@SuppressWarnings("unchecked")
		S[] expandedArray = (S[]) new Object[newlength];
		System.arraycopy(array, 0, expandedArray, 0, array.length);
		return expandedArray;
	}

	public static byte[][] expand(byte[][] array, int next) {
		int newlength = array.length;
		while (newlength < next + 1) {
			newlength *= 2;
		}
		byte[][] expandedArray = new byte[newlength][];
		System.arraycopy(array, 0, expandedArray, 0, array.length);
		return expandedArray;
	}

	public static char[][] expand(char[][] array, int next) {
		int newlength = array.length;
		while (newlength < next + 1) {
			newlength *= 2;
		}
		char[][] expandedArray = new char[newlength][];
		System.arraycopy(array, 0, expandedArray, 0, array.length);
		return expandedArray;
	}

	public static byte[] join(byte[] bytes, byte b) {
		int pos = binarySearch(bytes, b);
		if (pos < 0) {
			int ins = -(pos + 1);
			byte[] newAlts = new byte[bytes.length + 1];
			System.arraycopy(bytes, 0, newAlts, 0, ins);
			newAlts[ins] = b;
			System.arraycopy(bytes, ins, newAlts, ins + 1, bytes.length - ins);
			return newAlts;
		} else {
			return bytes;
		}
	}

	public static char[] join(char[] chars, char c) {
		int pos = binarySearch(chars, c);
		if (pos < 0) {
			int ins = -(pos + 1);
			char[] newAlts = new char[chars.length + 1];
			System.arraycopy(chars, 0, newAlts, 0, ins);
			newAlts[ins] = c;
			System.arraycopy(chars, ins, newAlts, ins + 1, chars.length - ins);
			return newAlts;
		} else {
			return chars;
		}
	}

	public static boolean verify(byte[] bytes, int i, byte[] tail) {
		if (bytes.length - i != tail.length) {
			return false;
		}
		for (int j = 0; j < tail.length; j++) {
			if (bytes[i + j] != tail[j]) {
				return false;
			}
		}
		return true;
	}

	public static boolean verify(char[] chars, int i, char[] tail) {
		if (chars.length - i != tail.length) {
			return false;
		}
		for (int j = 0; j < tail.length; j++) {
			if (chars[i + j] != tail[j]) {
				return false;
			}
		}
		return true;
	}

	public static byte[] suffix(byte[] chars, int pos) {
		int len = chars.length;
		if (pos == len) {
			return NO_BYTES;
		}
		return copyOfRange(chars, pos, len);
	}

	public static char[] suffix(char[] chars, int pos) {
		int len = chars.length;
		if (pos == len) {
			return NO_CHARS;
		}
		return copyOfRange(chars, pos, len);
	}

}
