package net.amygdalum.util.tries;

import java.util.Collections;
import java.util.Iterator;

public interface CharTrieCursor<T> extends Iterable<T> {

	@SuppressWarnings("rawtypes")
	public static final CharTrieCursor NULL = new CharTrieCursor() {

		@Override
		public Iterator iterator() {
			return Collections.emptyIterator();
		}

		@Override
		public void reset() {
		}

		@Override
		public boolean lookahead(char c) {
			return false;
		}

		@Override
		public boolean accept(char c) {
			return false;
		}

		@Override
		public boolean hasAttachments() {
			return false;
		}
		
	};

	void reset();

	boolean lookahead(char c);
	boolean accept(char c);

	boolean hasAttachments();

}