package net.amygdalum.util.tries;

import java.util.Collections;
import java.util.Iterator;

public interface ByteTrieCursor<T> extends Iterable<T> {

	@SuppressWarnings("rawtypes")
	public static final ByteTrieCursor NULL = new ByteTrieCursor() {

		@Override
		public Iterator iterator() {
			return Collections.emptyIterator();
		}

		@Override
		public void reset() {
		}

		@Override
		public boolean lookahead(byte b) {
			return false;
		}

		@Override
		public boolean accept(byte b) {
			return false;
		}

		@Override
		public boolean hasAttachments() {
			return false;
		}
		
	};

	void reset();

	boolean lookahead(byte b);
	boolean accept(byte b);

	boolean hasAttachments();

}