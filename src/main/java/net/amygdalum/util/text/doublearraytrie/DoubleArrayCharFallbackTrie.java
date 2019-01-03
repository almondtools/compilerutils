package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.CharAutomaton;
import net.amygdalum.util.text.CharFallbackNavigator;
import net.amygdalum.util.text.CharTrie;
import net.amygdalum.util.text.WordSetNavigationException;

public class DoubleArrayCharFallbackTrie<T> implements CharTrie<T> {

	private static final int INITIAL_SIZE = 1024;
	private static final int MAX_SPACE = Character.MAX_VALUE;

	private static final int STOP = -1;

	private int[] base;
	private int[] check;
	private int[] fallback;
	private char[][] alts;
	private T[] attachments;

	private int nextCheck;

	@SuppressWarnings("unchecked")
	public DoubleArrayCharFallbackTrie() {
		this.base = new int[INITIAL_SIZE];
		this.check = new int[INITIAL_SIZE];
		this.fallback = new int[INITIAL_SIZE];
		this.alts = new char[INITIAL_SIZE][];
		this.attachments = (T[]) new Object[INITIAL_SIZE];
		this.nextCheck = 1;
	}

	private static int key(char c) {
		return (int) c + 1;
	}

	private static int minKey(char... input) {
		char min = Character.MAX_VALUE;
		for (char c : input) {
			if (c < min) {
				min = c;
			}
		}
		return key(min);
	}

	private int freebase(char... input) {
		if (input.length == 0) {
			return -1;
		}
		int pivotKey = minKey(input);
		int predictedNext = Math.max(pivotKey + 1, nextCheck);

		ensureSufficientLength(predictedNext);
		while (check[predictedNext] != 0) {
			predictedNext++;
			ensureSufficientLength(predictedNext);
		}
		nextCheck = predictedNext;

		int nextbase = -1;
		int blocked = 0;
		while (predictedNext < Integer.MAX_VALUE) {
			ensureSufficientLength(predictedNext + MAX_SPACE);
			if (check[predictedNext] != 0) {
				blocked++;
				predictedNext++;
				continue;
			}
			nextbase = predictedNext - pivotKey;
			boolean found = true;
			for (char c : input) {
				int next = nextbase + key(c);
				if (check[next] != 0) {
					found = false;
					break;
				}
			}
			if (found) {
				break;
			} else {
				predictedNext++;
			}
		}
		int checked = predictedNext - nextCheck;
		int free = checked - blocked;
		if ((checked >> 5) > free) {
			nextCheck = predictedNext;
		}
		return nextbase;
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
	public CharAutomaton<T> cursor() {
		return new Cursor();
	}

	@Override
	public boolean contains(char[] chars) {
		int state = 1;
		for (int i = 0; i < chars.length; i++) {
			int statebase = base[state];
			if (statebase < 0) {
				return false;
			}
			char c = chars[i];
			int next = statebase + key(c);
			if (next >= check.length || check[next] != state) {
				return false;
			}
			state = next;
		}
		return attachments[state] != null;
	}

	@Override
	public T find(char[] chars) {
		int state = 1;
		for (int i = 0; i < chars.length; i++) {
			int statebase = base[state];
			if (statebase < 0) {
				return null;
			}
			char c = chars[i];
			int next = statebase + key(c);
			if (next >= check.length || check[next] != state) {
				return null;
			}
			state = next;
		}
		return attachments[state];
	}

	@Override
	public CharFallbackNavigator<T, ?> navigator() {
		return new Navigator(1);
	}

	public static class Builder<T> {

		private DoubleArrayCharFallbackTrie<T> trie;

		public Builder() {
			this.trie = new DoubleArrayCharFallbackTrie<T>();
		}

		public int root() {
			return 1;
		}

		public int[] insert(int state, char... alternatives) {
			assert trie.base[state] == 0 && trie.alts[state] == null;
			int[] nexts = new int[alternatives.length];

			int newbase = trie.freebase(alternatives);
			trie.base[state] = newbase;
			trie.alts[state] = Arrays.sorted(alternatives);
			for (int i = 0; i < alternatives.length; i++) {
				char c = alternatives[i];
				int next = newbase + key(c);
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

		public DoubleArrayCharFallbackTrie<T> build() {
			return trie;
		}

	}

	private class Navigator implements CharFallbackNavigator<T, Navigator>, AttachmentAdaptor<T> {

		private int state;

		public Navigator(int state) {
			this.state = state;
		}

		@Override
		public Navigator nextNode(char c) {
			int next = base[state] + key(c);
			if (next < check.length && check[next] == state) {
				state = next;
			} else {
				throw new WordSetNavigationException("unexpected navigation to " + c);
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

	private class Cursor implements CharAutomaton<T> {

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
		public boolean lookahead(char c) {
			int statebase = base[state];
			int next = statebase + key(c);
			return next < check.length
				&& check[next] == state;
		}

		@Override
		public boolean accept(char c) {
			int statebase = base[state];
			while (statebase < 0) {
				state = fallback[state];
				if (state == 0) {
					reset();
					return false;
				}
				statebase = base[state];
			}
			int next = statebase + key(c);
			while (state > 1 && (next >= check.length || check[next] != state)) {
				state = fallback[state];
				statebase = base[state];
				next = statebase + key(c);
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
