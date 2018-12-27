package net.amygdalum.util.text;

import java.util.Collections;
import java.util.Iterator;

public interface ByteAutomaton<T> extends Iterable<T> {

	@SuppressWarnings("rawtypes")
	public static final ByteAutomaton NULL = new ByteAutomaton() {

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