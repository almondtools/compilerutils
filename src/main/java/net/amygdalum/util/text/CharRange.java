package net.amygdalum.util.text;

import static java.util.Arrays.asList;
import static net.amygdalum.util.text.CharUtils.after;
import static net.amygdalum.util.text.CharUtils.before;

import java.util.List;

public class CharRange {

	public char from;
	public char to;

	public CharRange(char from, char to) {
		this.from = from;
		this.to = to;
	}

	public boolean contains(char value) {
		return value >= from
			&& value <= to;
	}


	public List<CharRange> splitBefore(char value) {
		if (value == from) {
			return asList(this);
		} else {
			return asList(
				new CharRange(from, before(value)),
				new CharRange(value, to));
		}
	}

	public List<CharRange> splitAfter(char value) {
		if (value == to) {
			return asList(this);
		} else {
			return asList(
				new CharRange(from, value),
				new CharRange(after(value), to));
		}
	}

	public List<CharRange> splitAround(char from, char to) {
		boolean fromStart = this.from == from;
		boolean toEnd = this.to == to;
		if (fromStart && toEnd) {
			return asList(this);
		} else if (fromStart) {
			return asList(
				new CharRange(from, to),
				new CharRange(after(to), this.to));
		} else if (toEnd) {
			return asList(
				new CharRange(this.from, before(from)),
				new CharRange(from, to));
		} else {
			return asList(
				new CharRange(this.from, before(from)),
				new CharRange(from, to),
				new CharRange(after(to), this.to));
		}
	}

	@Override
	public int hashCode() {
		return (int) from * 7
			+ (int) to * 3;
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
		CharRange that = (CharRange) obj;
		return this.from == that.from
			&& this.to == that.to;
	}

	@Override
	public String toString() {
		return from + "-" + to;
	}

}
