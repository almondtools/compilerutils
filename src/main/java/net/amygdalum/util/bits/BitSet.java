package net.amygdalum.util.bits;

import java.util.Arrays;

public class BitSet implements Cloneable {

	private final int size;
	private final int lastsize;
	private final long lastmask;
	private long[] bits;

	private BitSet(int size, long[] bits) {
		this.size = size;
		this.lastsize = ((size - 1) % 64) + 1;
		this.lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		this.bits = bits;
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
		long lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		
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
		long[] notbits = new long[bits.length];
		for (int i = 0; i < notbits.length; i++) {
			notbits[i] = ~bits[i];
		}
		notbits[notbits.length - 1] &= lastmask;
		return new BitSet(size, notbits);
	}

	public BitSet and(BitSet other) {
		int andsize = Math.max(size, other.size);
		int loopsize = Math.min(bits.length, other.bits.length);
		long[] andbits = init(andsize);
		for (int i = 0; i < loopsize; i++) {
			andbits[i] = bits[i] & other.bits[i];
		}
		return new BitSet(andsize, andbits);
	}

	public BitSet andNot(BitSet other) {
		int andsize = Math.max(size, other.size);
		int lastsize = ((andsize - 1) % 64) + 1;
		long lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		int loopsize = Math.min(bits.length, other.bits.length);
		long[] andbits = init(andsize);
		for (int i = 0; i < loopsize; i++) {
			andbits[i] = bits[i] & ~other.bits[i];
		}
		andbits[andbits.length - 1] &= lastmask;
		return new BitSet(andsize, andbits);
	}

	public BitSet or(BitSet other) {
		int orsize = Math.max(size, other.size);
		int loopsize = Math.max(bits.length, other.bits.length);
		long[] orbits = init(orsize);
		for (int i = 0; i < loopsize; i++) {
			if (bits.length <= i) {
				orbits[i] = other.bits[i];
			} else if (other.bits.length <= i) {
				orbits[i] = bits[i];
			} else {
				orbits[i] = bits[i] | other.bits[i];
			}
		}
		return new BitSet(orsize, orbits);
	}

	public BitSet orNot(BitSet other) {
		int andsize = Math.max(size, other.size);
		int lastsize = ((andsize - 1) % 64) + 1;
		long lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		int loopsize = Math.min(bits.length, other.bits.length);
		long[] andbits = init(andsize);
		for (int i = 0; i < loopsize; i++) {
			andbits[i] = bits[i] | ~other.bits[i];
		}
		andbits[andbits.length - 1] &= lastmask;
		return new BitSet(andsize, andbits);
	}

	public BitSet xor(BitSet other) {
		int andsize = Math.max(size, other.size);
		int lastsize = ((andsize - 1) % 64) + 1;
		long lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		int loopsize = Math.min(bits.length, other.bits.length);
		long[] andbits = init(andsize);
		for (int i = 0; i < loopsize; i++) {
			andbits[i] = bits[i] ^ other.bits[i];
		}
		andbits[andbits.length - 1] &= lastmask;
		return new BitSet(andsize, andbits);
	}

	public BitSet eq(BitSet other) {
		int andsize = Math.max(size, other.size);
		int lastsize = ((andsize - 1) % 64) + 1;
		long lastmask = lastsize == 64 ? 0xffff_ffff_ffff_ffffl : (1l << lastsize) - 1;
		int loopsize = Math.min(bits.length, other.bits.length);
		long[] andbits = init(andsize);
		for (int i = 0; i < loopsize; i++) {
			andbits[i] = ~(bits[i] ^ other.bits[i]);
		}
		andbits[andbits.length - 1] &= lastmask;
		return new BitSet(andsize, andbits);
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
