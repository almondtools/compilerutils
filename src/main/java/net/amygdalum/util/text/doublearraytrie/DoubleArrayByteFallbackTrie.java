package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;
import static net.amygdalum.util.text.doublearraytrie.Arrays.join;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.ByteAutomaton;
import net.amygdalum.util.text.ByteFallbackAdaptor;
import net.amygdalum.util.text.ByteNode;

public class DoubleArrayByteFallbackTrie<T> implements DoubleArrayByteTrie<T> {

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
		this.base[1] = 1;
		this.alts[1] = new byte[0];
	}

	private static int key(byte b) {
		return ((int) b) + 129;
	}

	@Override
	public void insert(byte[] bytes, T out) {
		int state = 1;
		for (int i = 0; i < bytes.length; i++) {
			int statebase = base[state];
			if (statebase <= 0) {
				statebase = xcheck(bytes[i]);
				base[state] = statebase;
			}
			byte b = bytes[i];
			int next = statebase + key(b);
			ensureSufficientLength(next);
			if (check[next] == 0) {
				check[next] = state;
				addAlt(state, b);
			} else if (check[next] != state) {
				int collidingstate = check[next];

				byte[] altsCurrent = alts[state];
				byte[] altsColliding = alts[collidingstate];

				if (altsCurrent.length + 1 <= altsColliding.length || check[state] == collidingstate) {
					int newbase = xcheck(join(altsCurrent, b));
					remap(state, newbase, altsCurrent);
					next = base[state] + key(b);
				} else {
					int newbase = xcheck(altsColliding);
					remap(collidingstate, newbase, altsColliding);
				}

				check[next] = state;
				addAlt(state, b);
			}
			state = next;
		}
		if (base[state] == 0) {
			base[state] = STOP;
			alts[state] = new byte[0];
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

	private void addAlt(int state, byte b) {
		byte[] bytes = alts[state];
		if (bytes != null) {
			alts[state] = join(bytes, b);
		} else {
			alts[state] = new byte[] {b};
		}
	}

	private void remap(int remap, int newbase, byte[] alternatives) {
		int remapbase = base[remap];

		base[remap] = newbase;

		for (byte ab : alternatives) {
			int abremap = remapbase + key(ab);
			int abnew = newbase + key(ab);
			base[abnew] = base[abremap];
			check[abnew] = check[abremap];
			alts[abnew] = alts[abremap];
			attachments[abnew] = attachments[abremap];
			if (base[abremap] > 0) {
				byte[] altsremap = alts[abremap];
				for (byte rb : altsremap) {
					check[base[abremap] + key(rb)] = abnew;
				}
			}
			base[abremap] = 0;
			check[abremap] = 0;
			alts[abremap] = null;
			attachments[abremap] = null;
		}
	}

	private int xcheck(byte... input) {
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

	@Override
	public ByteAutomaton<T> cursor() {
		return new Cursor<>(base, check, fallback, attachments);
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
	public ByteNode<T> asNode() {
		return new NodeAdaptors<>(base, check, fallback, alts, attachments)
			.fetch(1);
	}

	private static class NodeAdaptors<S> {

		private int[] base;
		private int[] check;
		private int[] fallback;
		private byte[][] alts;
		private S[] attachments;
		
		private AdaptorNode<S>[] nodes;

		@SuppressWarnings("unchecked")
		public NodeAdaptors(int[] base, int[] check, int[] fallback, byte[][] alts, S[] attachments) {
			this.base = base;
			this.check = check;
			this.fallback = fallback;
			this.alts = alts;
			this.attachments = attachments;
			this.nodes = new AdaptorNode[base.length];
		}

		public ByteNode<S> fetch(int state) {
			AdaptorNode<S> node = nodes[state];
			if (node == null) {
				node = new AdaptorNode<>(this, state);
				nodes[state] = node;
			}
			return node;
		}

	}

	private static class AdaptorNode<S> implements ByteNode<S>, AttachmentAdaptor<S>, ByteFallbackAdaptor<S> {

		private NodeAdaptors<S> adaptors;
		private int state;

		public AdaptorNode(NodeAdaptors<S> adaptors, int state) {
			this.adaptors = adaptors;
			this.state = state;
		}

		@Override
		public ByteNode<S> nextNode(byte b) {
			int next = adaptors.base[state] + key(b);
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
		public byte[] getAlternatives() {
			return adaptors.alts[state];
		}

		@Override
		public ByteNode<S> getFallback() {
			int fallback = adaptors.fallback[state];
			if (fallback == 0) {
				return null;
			} else {
				return adaptors.fetch(fallback);
			}
		}

		@Override
		public void setFallback(ByteNode<S> fallbackNode) {
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

	private static class Cursor<S> implements ByteAutomaton<S> {

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
