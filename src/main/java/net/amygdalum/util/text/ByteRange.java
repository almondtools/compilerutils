package net.amygdalum.util.text;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.List;

public class ByteRange {

	private static final int UNKNOWN = -1;
	public int length;
	public byte[] from;
	public byte[] to;
	private int size;

	public ByteRange(byte[] from, byte[] to) {
		this(from, to, UNKNOWN);
	}

	public ByteRange(byte[] from, byte[] to, int size) {
		this.from = from;
		this.to = to;
		this.length = lengthOf(from, to);
		this.size = size;
	}

	public ByteRange(byte from, byte to) {
		this(from, to, UNKNOWN);
	}

	public ByteRange(byte from, byte to, int size) {
		this.size = size;
		this.from = new byte[] { from };
		this.to = new byte[] { to };
		this.length = 1;
	}

	private int lengthOf(byte[] from, byte[] to) {
		if (from.length != to.length) {
			throw new IllegalArgumentException();
		}
		return from.length;
	}

	public boolean contains(byte... value) {
		if (value.length != length) {
			return false;
		}
		for (int i = 0; i < length; i++) {
			int fromI = from[i] & 0xff;
			int toI = to[i] & 0xff;
			int valueI = value[i] & 0xff;
			if (fromI > valueI || toI < valueI) {
				return false;
			} else if (fromI < valueI && toI > valueI) {
				return true;
			}
		}
		return true;
	}

	public boolean ranges(byte from, byte to) {
		return this.length == 1
			&& this.from[0] == from
			&& this.to[0] == to;
	}

	public boolean ranges(byte[] from, byte[] to) {
		return this.length == from.length 
			&& Arrays.equals(this.from, from)
			&& this.length == to.length 
			&& Arrays.equals(this.to, to);
	}

	public int size() {
		return size;
	}

	public List<ByteRange> splitBefore(byte... value) {
		if (length != value.length) {
			return asList(this);
		}
		if (Arrays.equals(value, from)) {
			return asList(this);
		} else {
			return asList(
				new ByteRange(from, before(value), UNKNOWN),
				new ByteRange(value, to, UNKNOWN));
		}
	}

	public List<ByteRange> splitAfter(byte... value) {
		if (length != value.length) {
			return asList(this);
		}
		if (Arrays.equals(value, to)) {
			return asList(this);
		} else {
			return asList(
				new ByteRange(from, value, UNKNOWN),
				new ByteRange(after(value), to, UNKNOWN));
		}
	}

	public List<ByteRange> splitAround(byte from, byte to) {
		if (length != 1) {
			return asList(this);
		}
		boolean fromStart = this.from[0] == from;
		boolean toEnd = this.to[0] == to;
		if (fromStart && toEnd) {
			return asList(this);
		} else if (fromStart) {
			return asList(
				new ByteRange(from, to, UNKNOWN),
				new ByteRange(after(to), this.to, UNKNOWN));
		} else if (toEnd) {
			return asList(
				new ByteRange(this.from, before(from), UNKNOWN),
				new ByteRange(from, to, UNKNOWN));
		} else {
			return asList(
				new ByteRange(this.from, before(from), UNKNOWN),
				new ByteRange(from, to, UNKNOWN),
				new ByteRange(after(to), this.to, UNKNOWN));
		}
	}

	public List<ByteRange> splitAround(byte[] from, byte[] to) {
		if (length != from.length || length != to.length) {
			return asList(this);
		}
		boolean fromStart = Arrays.equals(this.from, from);
		boolean toEnd = Arrays.equals(this.to, to);
		if (fromStart && toEnd) {
			return asList(this);
		} else if (fromStart) {
			return asList(
				new ByteRange(from, to, UNKNOWN),
				new ByteRange(after(to), this.to, UNKNOWN));
		} else if (toEnd) {
			return asList(
				new ByteRange(this.from, before(from), UNKNOWN),
				new ByteRange(from, to, UNKNOWN));
		} else {
			return asList(
				new ByteRange(this.from, before(from), UNKNOWN),
				new ByteRange(from, to, UNKNOWN),
				new ByteRange(after(to), this.to, UNKNOWN));
		}
	}

	private byte[] before(byte... value) {
		byte[] before = Arrays.copyOf(value, value.length);
		for (int i = before.length - 1; i >= 0; i--) {
			before[i]--;
			if (before[i] != -1) {
				return before;
			}
		}
		return before;
	}

	private byte[] after(byte... value) {
		byte[] after = Arrays.copyOf(value, value.length);
		for (int i = after.length - 1; i >= 0; i--) {
			after[i]++;
			if (after[i] != 0) {
				return after;
			}
		}
		return after;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(from) * 7
			+ Arrays.hashCode(to) * 3
			+ size;
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
		ByteRange that = (ByteRange) obj;
		return Arrays.equals(this.from, that.from)
			&& Arrays.equals(this.to, that.to)
			&& this.size == that.size;
	}

	@Override
	public String toString() {
		return Arrays.toString(from) + "-" + Arrays.toString(to) + ":" + size;
	}

}
