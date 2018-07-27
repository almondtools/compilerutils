package net.amygdalum.util.bits;

import java.util.Arrays;

public class BitSet implements Cloneable {

	private final int size;
	private final long lastmask;
	private long[] bits;

	private BitSet(int size, long[] bits) {
		this.size = size;
		this.lastmask = computeMask(size);
		this.bits = bits;
	}

	private static final long computeMask(int size) {
		int lastsize = ((size - 1) % 64) + 1;
		return lastsize == 64
			? 0xffff_ffff_ffff_ffffl
			: (1l << lastsize) - 1;
	}

	public static BitSet bits(int size, int... setbits) {
		long[] bits = init(size);
		for (int setbit : setbits) {
			int index = setbit / 64;
			int offset = setbit % 64;
			bits[index] |= 1l << offset;
		}
		return new BitSet(size, bits);
	}

	public static BitSet ofLong(long... bits) {
		return new BitSet(bits.length * 64, bits);
	}

	public static BitSet empty(int size) {
		return new BitSet(size, init(size));
	}

	public static BitSet all(int size) {
		return new BitSet(size, allBitsSet(size));
	}

	private static long[] allBitsSet(int size) {
		long[] bits = init(size);
		int lastsize = ((size - 1) % 64) + 1;
		long lastmask = lastsize == 64
			? 0xffff_ffff_ffff_ffffl
			: (1l << lastsize) - 1;

		for (int i = 0; i < bits.length; i++) {
			bits[i] = 0xffff_ffff_ffff_ffffl;
		}
		bits[bits.length - 1] &= lastmask;
		return bits;
	}

	public int bitCount() {
		int bitCount = 0;
		for (int i = 0; i < bits.length; i++) {
			bitCount += Long.bitCount(bits[i]);
		}
		return bitCount;
	}

	public int[] allClearBits() {
		int[] clearBits = new int[size - bitCount()];
		int index = 0;
		int next = 0;
		long value = ~bits[index];
		long mask = 1l;
		for (int i = 0; i < clearBits.length; i++) {
			while ((mask & value) == 0) {
				mask <<= 1;
				next++;
				if (mask == 0) {
					index++;
					if (index >= bits.length) {
						break;
					}
					value = ~bits[index];
					mask = 1l;
				}
			}
			clearBits[i] = next;
			mask <<= 1;
			next++;
			if (mask == 0) {
				index++;
				if (index >= bits.length) {
					break;
				}
				value = ~bits[index];
				mask = 1l;
			}
		}
		return clearBits;
	}

	public int[] allSetBits() {
		int[] setBits = new int[bitCount()];
		int index = 0;
		int next = 0;
		long value = bits[index];
		long mask = 1l;
		for (int i = 0; i < setBits.length; i++) {
			while ((mask & value) == 0) {
				mask <<= 1;
				next++;
				if (mask == 0) {
					index++;
					if (index >= bits.length) {
						break;
					}
					value = bits[index];
					mask = 1l;
				}
			}
			setBits[i] = next;
			mask <<= 1;
			next++;
			if (mask == 0) {
				index++;
				if (index >= bits.length) {
					break;
				}
				value = bits[index];
				mask = 1l;
			}
		}
		return setBits;
	}

	public int nextSetBit(int i) {
		if (i >= size) {
			return -1;
		}
		int index = i / 64;
		int start = i % 64;
		long value = bits[index];
		long mask = 1l << start;

		int next = i;
		while ((mask & value) == 0) {
			mask <<= 1;
			next++;
			if (mask == 0) {
				index++;
				if (index >= bits.length) {
					break;
				}
				value = bits[index];
				mask = 1l;
			}
		}
		if (next >= size) {
			return -1;
		} else {
			return next;
		}
	}

	public int nextClearBit(int i) {
		if (i >= size) {
			return -1;
		}
		int index = i / 64;
		int start = i % 64;
		long value = ~bits[index];
		long mask = 1l << start;

		int next = i;
		while ((mask & value) == 0) {
			mask <<= 1;
			next++;
			if (mask == 0) {
				index++;
				if (index >= bits.length) {
					break;
				}
				value = ~bits[index];
				mask = 1l;
			}
		}
		if (next >= size) {
			return -1;
		} else {
			return next;
		}
	}

	public int size() {
		return size;
	}

	public BitSet not() {
		long[] newbits = new long[bits.length];
		for (int i = 0; i < newbits.length; i++) {
			newbits[i] = ~bits[i];
		}
		newbits[newbits.length - 1] &= lastmask;
		return new BitSet(size, newbits);
	}

	public BitSet and(BitSet other) {
		int diff = bits.length - other.bits.length;
		int newsize = Math.max(size, other.size);
		long[] newbits = init(newsize);
		int len = diff <= 0 ? bits.length : other.bits.length;
		for (int i = 0; i < len; i++) {
			newbits[i] = bits[i] & other.bits[i];
		}
		return new BitSet(newsize, newbits);
	}

	public BitSet andNot(BitSet other) {
		int diff = bits.length - other.bits.length;
		int newsize = Math.max(size, other.size);
		int lastsize = ((newsize - 1) % 64) + 1;
		long lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		long[] newbits = init(newsize);
		int len = diff <= 0 ? bits.length : other.bits.length;
		for (int i = 0; i < len; i++) {
			newbits[i] = bits[i] & ~other.bits[i];
		}
		if (diff > 0) {
			System.arraycopy(bits, len, newbits, len, diff);
		}
		newbits[newbits.length - 1] &= lastmask;
		return new BitSet(newsize, newbits);
	}

	public BitSet or(BitSet other) {
		int diff = bits.length - other.bits.length;
		int newsize = Math.max(size, other.size);
		long[] newbits = init(newsize);
		int len = diff <= 0 ? bits.length : other.bits.length;
		for (int i = 0; i < len; i++) {
			newbits[i] = bits[i] | other.bits[i];
		}
		if (diff > 0) {
			System.arraycopy(bits, len, newbits, len, diff);
		} else if (diff < 0) {
			System.arraycopy(other.bits, len, newbits, len, -diff);
		}
		return new BitSet(newsize, newbits);
	}

	public BitSet orNot(BitSet other) {
		int diff = bits.length - other.bits.length;
		int newsize = Math.max(size, other.size);
		int lastsize = ((newsize - 1) % 64) + 1;
		long lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		int len = diff <= 0 ? bits.length : other.bits.length;
		long[] newbits = init(newsize);
		for (int i = 0; i < len; i++) {
			newbits[i] = bits[i] | ~other.bits[i];
		}
		if (diff > 0) {
			Arrays.fill(newbits, len, len + diff, 0xffff_ffff_ffff_ffffl);
		} else if (diff < 0) {
			for (int i = len; i < other.bits.length; i++) {
				newbits[i] = ~other.bits[i];
			}
		}
		newbits[newbits.length - 1] &= lastmask;
		return new BitSet(newsize, newbits);
	}

	public BitSet xor(BitSet other) {
		int diff = bits.length - other.bits.length;
		int newsize = Math.max(size, other.size);
		int lastsize = ((newsize - 1) % 64) + 1;
		long lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		int len = diff <= 0 ? bits.length : other.bits.length;
		long[] newbits = init(newsize);
		for (int i = 0; i < len; i++) {
			newbits[i] = bits[i] ^ other.bits[i];
		}
		newbits[newbits.length - 1] &= lastmask;
		return new BitSet(newsize, newbits);
	}

	public BitSet eq(BitSet other) {
		int diff = bits.length - other.bits.length;
		int newsize = Math.max(size, other.size);
		int lastsize = ((newsize - 1) % 64) + 1;
		long lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		int len = diff <= 0 ? bits.length : other.bits.length;
		long[] newbits = init(newsize);
		for (int i = 0; i < len; i++) {
			newbits[i] = ~(bits[i] ^ other.bits[i]);
		}
		if (diff > 0) {
			for (int i = len; i < bits.length; i++) {
				newbits[i] = ~bits[i];
			}
		} else if (diff < 0) {
			for (int i = len; i < other.bits.length; i++) {
				newbits[i] = ~other.bits[i];
			}
		}
		newbits[newbits.length - 1] &= lastmask;
		return new BitSet(newsize, newbits);
	}

	public boolean isEmpty() {
		for (int i = 0; i < bits.length; i++) {
			if (bits[i] != 0l) {
				return false;
			}
		}
		return true;
	}

	public boolean get(int i) {
		if (i >= size) {
			return false;
		}
		int index = i / 64;
		int offset = i % 64;
		return (bits[index] & (1l << offset)) != 0;
	}

	public void set(int i) {
		if (i >= size) {
			return;
		}
		int index = i / 64;
		int offset = i % 64;
		bits[index] |= 1l << offset;
	}

	public void clear(int i) {
		if (i >= size) {
			return;
		}
		int index = i / 64;
		int offset = i % 64;
		bits[index] &= ~(1l << offset);
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < size; i++) {
			if (i / 4 > 0 && i % 4 == 0) {
				buffer.insert(0, ' ');
			}
			if (get(i)) {
				buffer.insert(0, 1);
			} else {
				buffer.insert(0, 0);
			}
		}
		return buffer.toString();
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(bits) + size * 13;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BitSet that = (BitSet) obj;
		return this.size == that.size
			&& Arrays.equals(this.bits, that.bits);
	}

	@Override
	public BitSet clone() {
		try {
			BitSet clone = (BitSet) super.clone();
			clone.bits = Arrays.copyOf(bits, bits.length);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	private static long[] init(int size) {
		return new long[((size - 1) / 64) + 1];
	}

}
