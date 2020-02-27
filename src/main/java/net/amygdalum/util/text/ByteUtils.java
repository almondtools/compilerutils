package net.amygdalum.util.text;

import java.util.ArrayList;
import java.util.List;

public final class ByteUtils {

	public static final int BYTE_RANGE = 1 << 8;

	private ByteUtils() {
	}

	public static byte after(byte b) {
		return (byte) (b + 1);
	}

	public static byte before(byte b) {
		return (byte) (b - 1);
	}

	public static byte[] revert(byte[] bytes) {
		final int ri = bytes.length - 1;
		byte[] reversebytes = new byte[bytes.length];
		for (int i = 0; i < reversebytes.length; i++) {
			reversebytes[i] = bytes[ri - i];
		}
		return reversebytes;
	}

	public static int lastIndexOf(byte[] pattern, byte[] block) {
		nextPos: for (int i = pattern.length - block.length; i >= 0; i--) {
			for (int j = block.length - 1; j >= 0; j--) {
				if (pattern[j + i] == block[j]) {
					continue;
				} else {
					continue nextPos;
				}
			}
			return i;
		}
		return -1;
	}

	public static int minLength(List<byte[]> patterns) {
		int len = Integer.MAX_VALUE;
		for (byte[] pattern : patterns) {
			if (pattern.length < len) {
				len = pattern.length;
			}
		}
		return len;
	}

	public static int minLength(byte[][] patterns) {
		int len = Integer.MAX_VALUE;
		for (byte[] pattern : patterns) {
			if (pattern.length < len) {
				len = pattern.length;
			}
		}
		return len;
	}

	public static int maxLength(List<byte[]> patterns) {
		int len = Integer.MIN_VALUE;
		for (byte[] pattern : patterns) {
			if (pattern.length > len) {
				len = pattern.length;
			}
		}
		return len;
	}

	public static int maxLength(byte[][] patterns) {
		int len = Integer.MIN_VALUE;
		for (byte[] pattern : patterns) {
			if (pattern.length > len) {
				len = pattern.length;
			}
		}
		return len;
	}

	public static List<Byte> asList(byte[] bytes) {
		List<Byte> list = new ArrayList<>(bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			list.add(bytes[i]);
		}
		return list;
	}

	public static List<Byte> reverseList(byte[] bytes) {
		List<Byte> list = new ArrayList<>(bytes.length);
		for (int i = bytes.length - 1; i >= 0; i--) {
			list.add(bytes[i]);
		}
		return list;
	}

}
