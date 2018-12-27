package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;
import static net.amygdalum.util.text.doublearraytrie.Arrays.join;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.CharAutomaton;
import net.amygdalum.util.text.CharFallbackAdaptor;
import net.amygdalum.util.text.CharNode;

public class DoubleArrayCharFallbackTrie<T> implements DoubleArrayCharTrie<T> {

	private static final int INITIAL_SIZE = 1024;
	private static final int STOP = -1;

	private int[] base;
	private int[] check;
	private int[] fallback;
	private char[][] alts;
	private T[] attachments;

	@SuppressWarnings("unchecked")
	public DoubleArrayCharFallbackTrie() {
		this.base = new int[INITIAL_SIZE];
		this.check = new int[INITIAL_SIZE];
		this.fallback = new int[INITIAL_SIZE];
		this.alts = new char[INITIAL_SIZE][];
		this.attachments = (T[]) new Object[INITIAL_SIZE];
		this.base[1] = 1;
		this.alts[1] = new char[0];
	}

	private static int key(char b) {
		return (int) b + 1;
	}

	public void insert(char[] chars, T out) {
		int state = 1;
		for (int i = 0; i < chars.length; i++) {
			int statebase = base[state];
			if (statebase <= 0) {
				statebase = xcheck(chars[i]);
				base[state] = statebase;
			}
			char c = chars[i];
			int next = statebase + key(c);
			ensureSufficientLength(next);
			if (check[next] == 0) {
				check[next] = state;
				addAlt(state, c);
			} else if (check[next] != state) {
				int collidingstate = check[next];

				char[] altsCurrent = alts[state];
				char[] altsColliding = alts[collidingstate];

				if (altsCurrent.length + 1 <= altsColliding.length || check[state] == collidingstate) {
					int newbase = xcheck(join(altsCurrent, c));
					remap(state, newbase, altsCurrent);
					next = base[state] + key(c);
				} else {
					int newbase = xcheck(altsColliding);
					remap(collidingstate, newbase, altsColliding);
				}

				check[next] = state;
				addAlt(state, c);
			}
			state = next;
		}
		if (base[state] == 0) {
			base[state] = STOP;
			alts[state] = new char[0];
		}
		attachments[state] = out;
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

	private void addAlt(int state, char c) {
		char[] chars = alts[state];
		if (chars != null) {
			alts[state] = join(chars, c);
		} else {
			alts[state] = new char[] {c};
		}
	}

	private void remap(int remap, int newbase, char[] alternatives) {
		int remapbase = base[remap];

		base[remap] = newbase;

		for (char ac : alternatives) {
			int abremap = remapbase + key(ac);
			int abnew = newbase + key(ac);
			base[abnew] = base[abremap];
			check[abnew] = check[abremap];
			alts[abnew] = alts[abremap];
			attachments[abnew] = attachments[abremap];
			if (base[abremap] > 0) {
				char[] altsremap = alts[abremap];
				for (char rc : altsremap) {
					check[base[abremap] + key(rc)] = abnew;
				}
			}
			base[abremap] = 0;
			check[abremap] = 0;
			alts[abremap] = null;
			attachments[abremap] = null;
		}
	}

	private int xcheck(char... input) {
		int nextbase = 0;
		nextState: while (nextbase >= 0) {
			nextbase++;
			for (char c : input) {
				int next = nextbase + key(c);
				ensureSufficientLength(next);
				if (check[next] != 0) {
					continue nextState;
				}
			}
			return nextbase;
		}
		return -1;
	}

	@Override
	public CharAutomaton<T> cursor() {
		return new Cursor<>(base, check, fallback, attachments);
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

	public CharNode<T> asNode() {
		return new NodeAdaptors<>(base, check, fallback, alts, attachments)
			.fetch(1);
	}

	private static class NodeAdaptors<S> {

		private int[] base;
		private int[] check;
		private int[] fallback;
		private char[][] alts;
		private S[] attachments;
		
		private AdaptorNode<S>[] nodes;

		@SuppressWarnings("unchecked")
		public NodeAdaptors(int[] base, int[] check, int[] fallback, char[][] alts, S[] attachments) {
			this.base = base;
			this.check = check;
			this.fallback = fallback;
			this.alts = alts;
			this.attachments = attachments;
			this.nodes = new AdaptorNode[base.length];
		}

		public CharNode<S> fetch(int state) {
			AdaptorNode<S> node = nodes[state];
			if (node == null) {
				node = new AdaptorNode<>(this, state);
				nodes[state] = node;
			}
			return node;
		}

	}

	private static class AdaptorNode<S> implements CharNode<S>, AttachmentAdaptor<S>, CharFallbackAdaptor<S> {

		private NodeAdaptors<S> adaptors;
		private int state;

		public AdaptorNode(NodeAdaptors<S> adaptors, int state) {
			this.adaptors = adaptors;
			this.state = state;
		}

		@Override
		public CharNode<S> nextNode(char c) {
			int next = adaptors.base[state] + key(c);
			if (next < adaptors.check.length && adaptors.check[next] == state) {
				return adaptors.fetch(next);
			} else {
				return null;
			}
		}

		@Override
		public S getAttached() {
			return adaptors.attachments[state];
		}

		@Override
		public int getAlternativesSize() {
			return adaptors.alts[state].length;
		}

		@Override
		public char[] getAlternatives() {
			return adaptors.alts[state];
		}

		@Override
		public CharNode<S> getFallback() {
			int fallback = adaptors.fallback[state];
			if (fallback == 0) {
				return null;
			} else {
				return adaptors.fetch(fallback);
			}
		}

		@Override
		public void setFallback(CharNode<S> fallbackNode) {
			if (fallbackNode == null) {
				adaptors.fallback[state] = 0;
			} else {
				adaptors.fallback[state] = ((AdaptorNode<S>) fallbackNode).state;
			}
		}

		@Override
		public void attach(S attached) {
			adaptors.attachments[state] = attached;
		}

	}

	private static class Cursor<S> implements CharAutomaton<S> {

		private int[] base;
		private int[] check;
		private int[] fallback;
		private S[] attachments;
		private int state;
		private AttachmentIterator iterator;

		public Cursor(int[] base, int[] check, int[] fallback, S[] attachments) {
			this.base = base;
			this.check = check;
			this.fallback = fallback;
			this.attachments = attachments;
			this.state = 1;
			this.iterator = new AttachmentIterator();
		}

		@Override
		public Iterator<S> iterator() {
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
				statebase = base[state];
			}
			int next = statebase + key(c);
			while (state > 1 && next < check.length && check[next] != state) {
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

		private class AttachmentIterator implements Iterator<S> {

			private int state;
			private S last;

			public void init(int state) {
				this.state = state;
				this.last = null;
			}

			@Override
			public boolean hasNext() {
				while (state > 0) {
					S a = attachments[state];
					if (a != null && a != last) {
						return true;
					}
					state = fallback[state];
				}
				return false;
			}

			@Override
			public S next() {
				while (state > 0) {
					S a = attachments[state];
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
