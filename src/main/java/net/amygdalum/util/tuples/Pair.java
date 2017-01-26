package net.amygdalum.util.tuples;

import java.util.Objects;

public class Pair<L, R> {
	
	public L left;
	public R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public int hashCode() {
		return 7 
			+ ((left == null) ? 0 : left.hashCode() * 17)
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
		Pair that = (Pair) obj;
		return Objects.equals(this.left, that.left)
			&& Objects.equals(this.right, that.right);
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append('(')
			.append(left.toString())
			.append(',')
			.append(right.toString())
			.append(')').toString();
	}
	
}
