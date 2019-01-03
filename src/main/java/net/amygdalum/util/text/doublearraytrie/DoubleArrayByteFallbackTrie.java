package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.ByteAutomaton;
import net.amygdalum.util.text.ByteFallbackNavigator;
import net.amygdalum.util.text.ByteTrie;
import net.amygdalum.util.text.WordSetNavigationException;

public class DoubleArrayByteFallbackTrie<T> implements ByteTrie<T> {

	private static final int INITIAL_SIZE = 1024;

	private static final int STOP = -1;

	private int[] base;
	private int[] check;
	private int[] fallback;
	private byte[][] alts;
	private T[] attachments;

	@SuppressWarnings("unchecked")
	public DoubleArrayByteFallbackTrie() {
		this.base = new int[INITIAL_SIZE];
		this.check = new int[INITIAL_SIZE];
		this.fallback = new int[INITIAL_SIZE];
		this.alts = new byte[INITIAL_SIZE][];
		this.attachments = (T[]) new Object[INITIAL_SIZE];
	}

	private static int key(byte b) {
		return ((int) b) + 129;
	}

	private int freebase(byte... input) {
		int nextbase = 0;
		nextState: while (nextbase >= 0) {
			nextbase++;
			for (byte b : input) {
				int next = nextbase + key(b);
				ensureSufficientLength(next);
				if (check[next] != 0) {
					continue nextState;
				}
			}
			return nextbase;
		}
		return -1;
	}

	private void ensureSufficientLength(int next) {
		if (next >= check.length) {
			check = expand(check, next);
			base = expand(base, next);
			fallback = expand(fallback, next);
			alts = expand(alts, next);
			attachments = expand(attachments, next);
		}
	}

	@Override
	public ByteAutomaton<T> cursor() {
		return new Cursor();
	}

	@Override
	public boolean contains(byte[] chars) {
		int state = 1;
		for (int i = 0; i < chars.length; i++) {
			int statebase = base[state];
			if (statebase < 0) {
				return false;
			}
			byte b = chars[i];
			int next = statebase + key(b);
			if (next >= check.length || check[next] != state) {
				return false;
			}
			state = next;
		}
		return attachments[state] != null;
	}

	@Override
	public T find(byte[] bytes) {
		int state = 1;
		for (int i = 0; i < bytes.length; i++) {
			int statebase = base[state];
			if (statebase < 0) {
				return null;
			}
			byte b = bytes[i];
			int next = statebase + key(b);
			if (next >= check.length || check[next] != state) {
				return null;
			}
			state = next;
		}
		return attachments[state];
	}

	@Override
	public ByteFallbackNavigator<T, ?> navigator() {
		return new Navigator(1);
	}

	public static class Builder<T> {

		private DoubleArrayByteFallbackTrie<T> trie;

		public Builder() {
			this.trie = new DoubleArrayByteFallbackTrie<T>();
		}

		public int root() {
			return 1;
		}

		public int[] insert(int state, byte... alternatives) {
			assert trie.base[state] == 0 && trie.alts[state] == null;
			int[] nexts = new int[alternatives.length];

			int newbase = trie.freebase(alternatives);
			trie.base[state] = newbase;
			trie.alts[state] = Arrays.sorted(alternatives);
			for (int i = 0; i < alternatives.length; i++) {
				byte b = alternatives[i];
				int next = newbase + key(b);
				trie.check[next] = state;
				nexts[i] = next;
			}
			return nexts;
		}

		public void fallback(int state, int fallbackState) {
			trie.fallback[state] = fallbackState;
		}

		public void attach(int state, T out) {
			trie.attachments[state] = out;
		}

		public void terminate(int state) {
			trie.base[state] = STOP;
		}

		public DoubleArrayByteFallbackTrie<T> build() {
			return trie;
		}

	}

	private class Navigator implements ByteFallbackNavigator<T, Navigator>, AttachmentAdaptor<T> {

		private int state;

		public Navigator(int state) {
			this.state = state;
		}

		@Override
		public Navigator nextNode(byte b) {
			int next = base[state] + key(b);
			if (next < check.length && check[next] == state) {
				state = next;
			} else {
				throw new WordSetNavigationException("unexpected navigation to " + b);
			}
			return this;
		}

		@Override
		public Navigator fallback() {
			state = fallback[state];
			return this;
		}

		@Override
		public T getAttached() {
			return attachments[state];
		}

		@Override
		public void attach(T attached) {
			attachments[state] = attached;
		}
	}

	private class Cursor implements ByteAutomaton<T> {

		private int state;
		private AttachmentIterator iterator;

		public Cursor() {
			this.state = 1;
			this.iterator = new AttachmentIterator();
		}

		@Override
		public Iterator<T> iterator() {
			iterator.init(state);
			return iterator;
		}

		@Override
		public void reset() {
			this.state = 1;
		}

		@Override
		public boolean lookahead(byte b) {
			int statebase = base[state];
			int next = statebase + key(b);
			return next < check.length
				&& check[next] == state;
		}

		@Override
		public boolean accept(byte b) {
			int statebase = base[state];
			while (statebase < 0) {
				state = fallback[state];
				if (state == 0) {
					reset();
					return false;
				}
				statebase = base[state];
			}
			int next = statebase + key(b);
			while (state > 1 && next < check.length && check[next] != state) {
				state = fallback[state];
				statebase = base[state];
				next = statebase + key(b);
			}
			if (next >= check.length || check[next] != state) {
				reset();
				return false;
			}
			state = next;
			return true;
		}

		@Override
		public boolean hasAttachments() {
			return attachments[state] != null;
		}

		private class AttachmentIterator implements Iterator<T> {

			private int state;
			private T last;

			public void init(int state) {
				this.state = state;
				this.last = null;
			}

			@Override
			public boolean hasNext() {
				while (state > 0) {
					T a = attachments[state];
					if (a != null && a != last) {
						return true;
					}
					state = fallback[state];
				}
				return false;
			}

			@Override
			public T next() {
				while (state > 0) {
					T a = attachments[state];
					if (a != null && a != last) {
						last = a;
						state = fallback[state];
						return a;
					}
					state = fallback[state];
				}
				throw new NoSuchElementException();
			}
		}

	}

}
