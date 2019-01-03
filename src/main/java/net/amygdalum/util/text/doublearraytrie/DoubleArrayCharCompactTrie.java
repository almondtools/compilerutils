package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.CharUtils.charToString;
import static net.amygdalum.util.text.doublearraytrie.Arrays.NO_CHARS;
import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;
import static net.amygdalum.util.text.doublearraytrie.Arrays.join;
import static net.amygdalum.util.text.doublearraytrie.Arrays.suffix;
import static net.amygdalum.util.text.doublearraytrie.Arrays.verify;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.CharAutomaton;
import net.amygdalum.util.text.CharNavigator;
import net.amygdalum.util.text.CharTrie;
import net.amygdalum.util.text.WordSetNavigationException;

/**
 * A DoubleArrayCharCompactTrie is a Trie based on chars. It has following properties:
 * - acyclic (no back links, no support links)
 * - each node may be reached by exactly one node (i.e. a tree)
 * 
 * @param <T> the type of attachment storable in each graph node
 */
public class DoubleArrayCharCompactTrie<T> implements CharTrie<T> {

	private static final int INITIAL_SIZE = 1024;
	private static final int MAX_SPACE = Character.MAX_VALUE;

	private static final int STOP = -1;

	private int[] base;
	private int[] check;
	private char[][] tail;
	private char[][] alts;
	private T[] attachments;

	private int nextCheck;

	@SuppressWarnings("unchecked")
	public DoubleArrayCharCompactTrie() {
		this.base = new int[INITIAL_SIZE];
		this.check = new int[INITIAL_SIZE];
		this.tail = new char[INITIAL_SIZE][];
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
			tail = expand(tail, next);
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
				return verify(chars, i, tail[state]);
			}
			char c = chars[i];
			int next = statebase + key(c);
			if (next >= check.length || check[next] != state) {
				return false;
			}
			state = next;
		}
		if (tail[state] != null && tail[state].length == 0) {
			return true;
		}
		return false;
	}

	@Override
	public T find(char[] chars) {
		int state = 1;
		for (int i = 0; i < chars.length; i++) {
			int statebase = base[state];
			if (statebase < 0 && verify(chars, i, tail[state])) {
				return attachments[state];
			}
			char c = chars[i];
			int next = statebase + key(c);
			if (next >= check.length || check[next] != state) {
				return null;
			}
			state = next;
		}
		if (tail[state] != null && tail[state].length == 0) {
			return attachments[state];
		}
		return null;
	}

	@Override
	public CharNavigator<T, ?> navigator() {
		return new Navigator(1);
	}

	public static class Builder<T> {

		private DoubleArrayCharCompactTrie<T> trie;

		public Builder() {
			this.trie = new DoubleArrayCharCompactTrie<T>();
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

		public void attach(int state, char[] tail, T out) {
			assert trie.base[state] == 0 || tail.length == 0;
			trie.attachments[state] = out;
			if (trie.base[state] == 0) {
				if (tail.length == 0) {
					trie.tail[state] = NO_CHARS;
				} else {
					trie.tail[state] = tail;
				}
			} else {
				trie.tail[state] = NO_CHARS;
			}
		}

		public void terminate(int state) {
			trie.base[state] = STOP;
		}

		public DoubleArrayCharCompactTrie<T> build() {
			return trie;
		}

	}

	private class Navigator implements CharNavigator<T, Navigator>, AttachmentAdaptor<T> {

		private int state;
		private int tailpos;
		private char[] activeTail;

		public Navigator(int state) {
			this.state = state;
		}

		@Override
		public Navigator nextNode(char c) {
			int statebase = base[state];
			if (statebase < 0) {
				if (activeTail == null) {
					activeTail = tail[state];
					if (activeTail == null) {
						return null;
					}
					tailpos = 0;
				}
				if (tailpos >= activeTail.length) {
					throw new WordSetNavigationException("unexpected navigation to " + charToString(c));
				}
				if (activeTail[tailpos] != c) {
					throw new WordSetNavigationException("unexpected navigation to " + charToString(c));
				}
				tailpos++;
			} else {
				int next = statebase + key(c);
				if (next < check.length && check[next] == state) {
					state = next;
				} else {
					throw new WordSetNavigationException("unexpected navigation to " + charToString(c));
				}
			}
			return this;
		}

		@Override
		public T getAttached() {
			if (activeTail != null && tailpos == activeTail.length) {
				return attachments[state];
			} else if (tail[state] == NO_CHARS) {
				return attachments[state];
			}
			return null;
		}

		@Override
		public void attach(T out) {
			if (activeTail != null) {
				int oldpointer = state;
				int taili = 0;
				while (taili < tailpos) {
					char c = activeTail[taili];
					int nextbase = freebase(c);
					base[state] = nextbase;
					int next = nextbase + key(c);
					check[next] = state;
					addAlt(state, c);
					state = next;
					taili++;
				}

				int nextbase = freebase(activeTail[taili]);
				base[state] = nextbase;

				char tc = activeTail[taili];
				int tailnext = nextbase + key(tc);
				check[tailnext] = state;
				addAlt(state, tc);
				base[tailnext] = STOP;
				tail[tailnext] = suffix(tail[oldpointer], taili + 1);
				attachments[tailnext] = attachments[oldpointer];

				tail[oldpointer] = null;
				attachments[oldpointer] = null;

				tail[state] = NO_CHARS;
				attachments[state] = out;
			} else {
				if (tail[state] != null && tail[state].length > 0) {
					int oldpointer = state;
					char[] tailchars = tail[state];
					int taili = 0;
					char tc = tailchars[taili];
					int nextbase = freebase(tc);
					base[state] = nextbase;

					int tailnext = nextbase + key(tc);
					check[tailnext] = state;
					addAlt(state, tc);
					base[tailnext] = STOP;
					tail[tailnext] = suffix(tail[oldpointer], taili + 1);
					attachments[tailnext] = attachments[oldpointer];

					tail[state] = null;
					attachments[state] = null;
				}
				tail[state] = NO_CHARS;
				attachments[state] = out;
			}
		}

		private void addAlt(int state, char c) {
			char[] chars = alts[state];
			if (chars != null) {
				alts[state] = join(chars, c);
			} else {
				alts[state] = new char[] {c};
			}
		}

	}

	private class Cursor implements CharAutomaton<T> {

		private int state;
		private char[] activetail;
		private int tailposition;
		private AttachmentIterator iterator;

		public Cursor() {
			this.state = 1;
			this.activetail = base[state] == STOP ? tail[state] : null;
			this.tailposition = 0;
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
			this.activetail = base[state] == STOP ? tail[state] : null;
			this.tailposition = 0;
		}

		@Override
		public boolean lookahead(char c) {
			if (activetail != null) {
				return tailposition < activetail.length
					&& activetail[tailposition] == c;
			} else {
				int statebase = base[state];
				int next = statebase + key(c);
				return next < check.length
					&& check[next] == state;
			}
		}

		@Override
		public boolean accept(char c) {
			if (activetail != null) {
				if (tailposition >= activetail.length) {
					reset();
					return false;
				}
				char expectedc = activetail[tailposition];
				if (expectedc != c) {
					reset();
					return false;
				}
				tailposition++;
				return true;
			} else {
				int statebase = base[state];
				int next = statebase + key(c);
				if (next >= check.length || check[next] != state) {
					reset();
					return false;
				}
				state = next;
				if (tail[state] != null && tail[state].length > 0) {
					activetail = tail[state];
					tailposition = 0;
				}
				return true;
			}
		}

		@Override
		public boolean hasAttachments() {
			if (tail[state] == NO_CHARS || activetail != null && tailposition == activetail.length) {
				T a = attachments[state];
				if (a != null) {
					return true;
				}
			}
			return false;
		}

		private class AttachmentIterator implements Iterator<T> {

			private int state;

			public void init(int state) {
				this.state = state;
			}

			@Override
			public boolean hasNext() {
				if (state == 0) {
					return false;
				}
				if (tail[state] == NO_CHARS || activetail != null && tailposition == activetail.length) {
					return attachments[state] != null;
				}
				return false;
			}

			@Override
			public T next() {
				if (state == 0) {
					throw new NoSuchElementException();
				}
				if (tail[state] == NO_CHARS || activetail != null && tailposition == activetail.length) {
					T a = attachments[state];
					state = 0;
					return a;
				}
				throw new NoSuchElementException();
			}
		}

	}

}
