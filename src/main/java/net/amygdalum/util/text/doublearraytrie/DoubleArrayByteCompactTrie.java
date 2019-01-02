package net.amygdalum.util.text.doublearraytrie;

import static java.lang.Math.min;
import static net.amygdalum.util.text.doublearraytrie.Arrays.NO_BYTES;
import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;
import static net.amygdalum.util.text.doublearraytrie.Arrays.join;
import static net.amygdalum.util.text.doublearraytrie.Arrays.suffix;
import static net.amygdalum.util.text.doublearraytrie.Arrays.verify;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.ByteAutomaton;
import net.amygdalum.util.text.ByteNavigator;
import net.amygdalum.util.text.WordSetNavigationException;

/**
 * A DoubleArrayByteCompactTrie is a Trie based on bytes. It has following properties:
 * - acyclic (no back links, no support links)
 * - each node may be reached by exactly one node (i.e. a tree)
 * 
 * @param <T> the type of attachment storable in each graph node
 */
public class DoubleArrayByteCompactTrie<T> implements DoubleArrayByteTrie<T> {

	private static final int INITIAL_SIZE = 1024;

	private static final int STOP = -1;

	private int[] base;
	private int[] check;
	private byte[][] tail;
	private byte[][] alts;
	private T[] attachments;

	@SuppressWarnings("unchecked")
	public DoubleArrayByteCompactTrie() {
		this.base = new int[INITIAL_SIZE];
		this.check = new int[INITIAL_SIZE];
		this.tail = new byte[INITIAL_SIZE][];
		this.alts = new byte[INITIAL_SIZE][];
		this.attachments = (T[]) new Object[INITIAL_SIZE];
	}

	private static int key(byte b) {
		return ((int) b) + 129;
	}

	@Override
	public void insert(byte[] bytes, T out) {
		int state = 1;
		if (base[state] == 0) {
			base[state] = 1;
			alts[state] = new byte[0];
		}
		for (int i = 0; i < bytes.length; i++) {
			int statebase = base[state];
			if (statebase < 0) {
				byte[] tailbytes = tail[state];
				if (verify(bytes, i, tailbytes)) {
					//already inserted nothing todo
				} else {
					int oldpointer = state;
					int maxprefixlength = min(bytes.length - i, tailbytes.length);
					int taili = 0;
					while (taili < maxprefixlength) {
						byte b = bytes[taili + i];
						if (b != tailbytes[taili]) {
							break;
						}
						int nextbase = xcheck(b);
						base[state] = nextbase;
						int next = nextbase + key(b);
						check[next] = state;
						addAlt(state, b);
						state = next;
						taili++;
					}
					i += taili;

					boolean bytesremaining = i < bytes.length;
					boolean tailbytesremaining = taili < tailbytes.length;
					byte[] nextbytes;
					if (bytesremaining && tailbytesremaining) {
						nextbytes = new byte[] {bytes[i], tailbytes[taili]};
					} else if (bytesremaining) {
						nextbytes = new byte[] {bytes[i]};
					} else if (tailbytesremaining) {
						nextbytes = new byte[] {tailbytes[taili]};
					} else {
						nextbytes = new byte[0];
					}

					int nextbase = xcheck(nextbytes);
					base[state] = nextbase;

					if (tailbytesremaining) {
						byte tb = tailbytes[taili];
						int tailnext = nextbase + key(tb);
						check[tailnext] = state;
						addAlt(state, tb);
						base[tailnext] = STOP;
						tail[tailnext] = suffix(tail[oldpointer], taili + 1);
						attachments[tailnext] = attachments[oldpointer];

						tail[oldpointer] = null;
						attachments[oldpointer] = null;
					} else {
						tail[state] = NO_BYTES;
						attachments[state] = attachments[oldpointer];
						if (state != oldpointer) {
							tail[oldpointer] = null;
							attachments[oldpointer] = null;
						}
					}

					if (bytesremaining) {
						byte rb = bytes[i];
						int insnext = nextbase + key(rb);
						check[insnext] = state;
						addAlt(state, rb);
						base[insnext] = STOP;
						tail[insnext] = suffix(bytes, i + 1);
						attachments[insnext] = out;
					} else {
						tail[state] = NO_BYTES;
						attachments[state] = out;
					}
				}
				return;
			}
			byte b = bytes[i];
			int next = statebase + key(b);
			if (next >= check.length) {
				check = expand(check, next);
				base = expand(base, next);
				tail = expand(tail, next);
				alts = expand(alts, next);
				attachments = expand(attachments, next);
			}
			if (check[next] == 0) {
				check[next] = state;
				addAlt(state, b);
				base[next] = STOP;
				tail[next] = suffix(bytes, i + 1);
				attachments[next] = out;
				return;
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
				base[next] = STOP;
				tail[next] = suffix(bytes, i + 1);
				attachments[next] = out;
				return;
			}
			state = next;
		}
		if (tail[state] != null && tail[state].length > 0) {
			int oldpointer = state;
			byte[] tailbytes = tail[state];
			int taili = 0;
			byte tb = tailbytes[taili];
			int nextbase = xcheck(tb);
			base[state] = nextbase;

			int tailnext = nextbase + key(tb);
			check[tailnext] = state;
			addAlt(state, tb);
			base[tailnext] = STOP;
			tail[tailnext] = suffix(tail[oldpointer], taili + 1);
			attachments[tailnext] = attachments[oldpointer];

			tail[state] = null;
			attachments[state] = null;
		}
		tail[state] = NO_BYTES;
		attachments[state] = out;
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
			tail[abnew] = tail[abremap];
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
			tail[abremap] = null;
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
	public ByteAutomaton<T> cursor() {
		return new Cursor();
	}

	@Override
	public boolean contains(byte[] bytes) {
		int state = 1;
		for (int i = 0; i < bytes.length; i++) {
			int statebase = base[state];
			if (statebase < 0) {
				return verify(bytes, i, tail[state]);
			}
			byte b = bytes[i];
			int next = statebase + key(b);
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
	public T find(byte[] bytes) {
		int state = 1;
		for (int i = 0; i < bytes.length; i++) {
			int statebase = base[state];
			if (statebase < 0 && verify(bytes, i, tail[state])) {
				return attachments[state];
			}
			byte b = bytes[i];
			int next = statebase + key(b);
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
	public ByteNavigator<T, ?> navigator() {
		return new Navigator(1);
	}

	public static class Builder<T> {

		private DoubleArrayByteCompactTrie<T> trie;

		public Builder() {
			this.trie = new DoubleArrayByteCompactTrie<T>();
		}

		public int root() {
			return 1;
		}

		public int[] insert(int state, byte... alternatives) {
			assert trie.base[state] == 0 && trie.alts[state] == null;
			int[] nexts = new int[alternatives.length];

			int newbase = freebase(alternatives);
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

		public void attach(int state, byte[] tail, T out) {
			assert trie.base[state] == 0 || tail.length == 0;
			trie.attachments[state] = out;
			if (trie.base[state] == 0) {
				if (tail.length == 0) {
					trie.tail[state] = NO_BYTES;
				} else {
					trie.tail[state] = tail;
				}
			} else {
				trie.tail[state] = NO_BYTES;
			}
		}

		public void terminate(int state) {
			trie.base[state] = STOP;
		}

		private int freebase(byte... input) {
			int nextbase = 0;
			nextState: while (nextbase >= 0) {
				nextbase++;
				for (byte b : input) {
					int next = nextbase + key(b);
					ensureSufficientLength(next);
					if (trie.check[next] != 0) {
						continue nextState;
					}
				}
				return nextbase;
			}
			return -1;
		}

		private void ensureSufficientLength(int next) {
			if (next >= trie.check.length) {
				trie.check = expand(trie.check, next);
				trie.base = expand(trie.base, next);
				trie.tail = expand(trie.tail, next);
				trie.alts = expand(trie.alts, next);
				trie.attachments = expand(trie.attachments, next);
			}
		}

		public DoubleArrayByteCompactTrie<T> build() {
			return trie;
		}

	}

	private class Navigator implements ByteNavigator<T, Navigator>, AttachmentAdaptor<T> {

		private int state;
		private int tailpos;
		private byte[] activeTail;

		public Navigator(int state) {
			this.state = state;
		}

		@Override
		public Navigator nextNode(byte b) {
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
					throw new WordSetNavigationException("unexpected navigation to " + b);
				}
				if (activeTail[tailpos] != b) {
					throw new WordSetNavigationException("unexpected navigation to " + b);
				}
				tailpos++;
			} else {
				int next = statebase + key(b);
				if (next < check.length && check[next] == state) {
					state = next;
				} else {
					throw new WordSetNavigationException("unexpected navigation to " + b);
				}
			}
			return this;
		}

		@Override
		public T getAttached() {
			if (activeTail != null && tailpos == activeTail.length) {
				return attachments[state];
			} else if (tail[state] == NO_BYTES) {
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
					byte b = activeTail[taili];
					int nextbase = xcheck(b);
					base[state] = nextbase;
					int next = nextbase + key(b);
					check[next] = state;
					addAlt(state, b);
					state = next;
					taili++;
				}

				int nextbase = xcheck(activeTail[taili]);
				base[state] = nextbase;

				byte tb = activeTail[taili];
				int tailnext = nextbase + key(tb);
				check[tailnext] = state;
				addAlt(state, tb);
				base[tailnext] = STOP;
				tail[tailnext] = suffix(tail[oldpointer], taili + 1);
				attachments[tailnext] = attachments[oldpointer];

				tail[oldpointer] = null;
				attachments[oldpointer] = null;

				tail[state] = NO_BYTES;
				attachments[state] = out;
			} else {
				if (tail[state] != null && tail[state].length > 0) {
					int oldpointer = state;
					byte[] tailbytes = tail[state];
					int taili = 0;
					byte tb = tailbytes[taili];
					int nextbase = xcheck(tb);
					base[state] = nextbase;

					int tailnext = nextbase + key(tb);
					check[tailnext] = state;
					addAlt(state, tb);
					base[tailnext] = STOP;
					tail[tailnext] = suffix(tail[oldpointer], taili + 1);
					attachments[tailnext] = attachments[oldpointer];

					tail[state] = null;
					attachments[state] = null;
				}
				tail[state] = NO_BYTES;
				attachments[state] = out;
			}
		}

	}

	private class Cursor implements ByteAutomaton<T> {

		private int state;
		private byte[] activetail;
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
		public boolean lookahead(byte b) {
			if (activetail != null) {
				return tailposition < activetail.length
					&& activetail[tailposition] == b;
			} else {
				int statebase = base[state];
				int next = statebase + key(b);
				return next < check.length
					&& check[next] == state;
			}
		}

		@Override
		public boolean accept(byte b) {
			if (activetail != null) {
				if (tailposition >= activetail.length) {
					reset();
					return false;
				}
				byte expectedb = activetail[tailposition];
				if (expectedb != b) {
					reset();
					return false;
				}
				tailposition++;
				return true;
			} else {
				int statebase = base[state];
				int next = statebase + key(b);
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
			if (tail[state] == NO_BYTES || activetail != null && tailposition == activetail.length) {
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
				if (tail[state] == NO_BYTES || activetail != null && tailposition == activetail.length) {
					return attachments[state] != null;
				}
				return false;
			}

			@Override
			public T next() {
				if (state == 0) {
					throw new NoSuchElementException();
				}
				if (tail[state] == NO_BYTES || activetail != null && tailposition == activetail.length) {
					T a = attachments[state];
					state = 0;
					return a;
				}
				throw new NoSuchElementException();
			}
		}

	}

}
