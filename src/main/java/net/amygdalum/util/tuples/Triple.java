package net.amygdalum.util.tuples;

import java.util.Objects;

public class Triple<L, M, R> {

	public L left;
	public M mid;
	public R right;

	public Triple(L left, M mid, R right) {
		this.left = left;
		this.mid = mid;
		this.right = right;
	}

	@Override
	public int hashCode() {
		return 13
			+ ((left == null) ? 0 : left.hashCode() * 17)
			+ ((mid == null) ? 0 : mid.hashCode() * 5)
			+ ((right == null) ? 0 : right.hashCode() * 31);
	}

	@SuppressWarnings("rawtypes")
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
		Triple that = (Triple) obj;
		return Objects.equals(this.left, that.left)
			&& Objects.equals(this.mid, that.mid)
			&& Objects.equals(this.right, that.right);
	}

	@Override
	public String toString() {
		return new StringBuilder().append('(')
			.append(left.toString())
			.append(',')
			.append(mid.toString())
			.append(',')
			.append(right.toString())
			.append(')').toString();
	}

}
