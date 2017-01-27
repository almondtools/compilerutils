package net.amygdalum.util.text;

import java.util.Arrays;

public class ByteRange {

	public byte[] from;
	public byte[] to;
	private int size;

	public ByteRange(byte[] from, byte[] to, int size) {
		this.from = from;
		this.to = to;
		this.size = size;
	}

	public ByteRange(byte from, byte to, int size) {
		this.size = size;
		this.from = new byte[] { from };
		this.to = new byte[] { to };
	}

	public int size() {
		return size;
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
