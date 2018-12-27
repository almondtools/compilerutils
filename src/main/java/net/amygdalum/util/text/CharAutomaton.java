package net.amygdalum.util.text;

import java.util.Collections;
import java.util.Iterator;

public interface CharAutomaton<T> extends Iterable<T> {

	@SuppressWarnings("rawtypes")
	public static final CharAutomaton NULL = new CharAutomaton() {

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