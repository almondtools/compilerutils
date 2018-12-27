package net.amygdalum.util.text.doublearraytrie;

import static java.lang.Math.min;
import static net.amygdalum.util.text.doublearraytrie.Arrays.NO_CHARS;
import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;
import static net.amygdalum.util.text.doublearraytrie.Arrays.join;
import static net.amygdalum.util.text.doublearraytrie.Arrays.suffix;
import static net.amygdalum.util.text.doublearraytrie.Arrays.verify;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.CharAutomaton;
import net.amygdalum.util.text.CharNode;

/**
 * A DoubleArrayCharTrie is a Trie based on chars. It has following properties:
 * - acyclic (no back links, no support links)
 * - each node may be reached by exactly one node (i.e. a tree)
 * 
 * @param <T> the type of attachment storable in each graph node
 */
public class DoubleArrayCharCompactTrie<T> implements DoubleArrayCharTrie<T> {

	private static final int INITIAL_SIZE = 1024;

	private static final int STOP = -1;

	private int[] base;
	private int[] check;
	private char[][] tail;
	private char[][] alts;
	private T[] attachments;

	@SuppressWarnings("unchecked")
	public DoubleArrayCharCompactTrie() {
		this.base = new int[INITIAL_SIZE];
		this.check = new int[INITIAL_SIZE];
		this.tail = new char[INITIAL_SIZE][];
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
			if (statebase < 0) {
				char[] tailchars = tail[state];
				if (verify(chars, i, tailchars)) {
					//already inserted nothing todo
				} else {
					int oldpointer = state;
					int maxprefixlength = min(chars.length - i, tailchars.length);
					int taili = 0;
					while (taili < maxprefixlength) {
						char c = chars[taili + i];
						if (c != tailchars[taili]) {
							break;
						}
						int nextbase = xcheck(c);
						base[state] = nextbase;
						int next = nextbase + key(c);
						check[next] = state;
						addAlt(state, c);
						state = next;
						taili++;
					}
					i += taili;

					boolean charsremaining = i < chars.length;
					boolean tailcharsremaining = taili < tailchars.length;
					char[] nextchars;
					if (charsremaining && tailcharsremaining) {
						nextchars = new char[] {chars[i], tailchars[taili]};
					} else if (charsremaining) {
						nextchars = new char[] {chars[i]};
					} else if (tailcharsremaining) {
						nextchars = new char[] {tailchars[taili]};
					} else {
						nextchars = new char[0];
					}

					int nextbase = xcheck(nextchars);
					base[state] = nextbase;

					if (tailcharsremaining) {
						char tc = tailchars[taili];
						int tailnext = nextbase + key(tc);
						check[tailnext] = state;
						addAlt(state, tc);
						base[tailnext] = STOP;
						tail[tailnext] = suffix(tail[oldpointer], taili + 1);
						attachments[tailnext] = attachments[oldpointer];

						tail[oldpointer] = null;
						attachments[oldpointer] = null;
					} else {
						tail[state] = NO_CHARS;
						attachments[state] = attachments[oldpointer];
						if (state != oldpointer) {
							tail[oldpointer] = null;
							attachments[oldpointer] = null;
						}
					}

					if (charsremaining) {
						char rc = chars[i];
						int insnext = nextbase + key(rc);
						check[insnext] = state;
						addAlt(state, rc);
						base[insnext] = STOP;
						tail[insnext] = suffix(chars, i + 1);
						attachments[insnext] = out;
					} else {
						tail[state] = NO_CHARS;
						attachments[state] = out;
					}
				}
				return;
			}
			char c = chars[i];
			int next = statebase + key(c);
			if (next >= check.length) {
				check = expand(check, next);
				base = expand(base, next);
				tail = expand(tail, next);
				alts = expand(alts, next);
				attachments = expand(attachments, next);
			}
			if (check[next] == 0) {
				check[next] = state;
				addAlt(state, c);
				base[next] = STOP;
				tail[next] = suffix(chars, i + 1);
				attachments[next] = out;
				return;
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
				base[next] = STOP;
				tail[next] = suffix(chars, i + 1);
				attachments[next] = out;
				return;
			}
			state = next;
		}
		if (tail[state] != null && tail[state].length > 0) {
			int oldpointer = state;
			char[] tailchars = tail[state];
			int taili = 0;
			char tc = tailchars[taili];
			int nextbase = xcheck(tc);
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
			tail[abnew] = tail[abremap];
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
			tail[abremap] = null;
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
				if (next >= check.length) {
					check = expand(check, next);
					base = expand(base, next);
					tail = expand(tail, next);
					alts = expand(alts, next);
					attachments = expand(attachments, next);
				}
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
		return new Cursor<>(base, check, tail, attachments);
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
	public CharNode<T> asNode() {
		return new NodeAdaptors<>(base, check, tail, alts, attachments)
			.fetch(1);
	}

	private static class NodeAdaptors<S> {

		private int[] base;
		private int[] check;
		private char[][] tail;
		private char[][] alts;
		private S[] attachments;

		private AdaptorNode<S>[] nodes;

		@SuppressWarnings("unchecked")
		public NodeAdaptors(int[] base, int[] check, char[][] tail, char[][] alts, S[] attachments) {
			this.base = base;
			this.check = check;
			this.tail = tail;
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

		public void attach(int state, S out) {
			if (tail[state] != null && tail[state].length > 0) {
				int oldpointer = state;
				char[] tailchars = tail[state];
				int taili = 0;
				char tc = tailchars[taili];
				int nextbase = xcheck(tc);
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

		public void attach(int state, int tailpos, S out) {
			char[] tailchars = tail[state];
			int oldpointer = state;
			int taili = 0;
			while (taili < tailpos) {
				char c = tailchars[taili];
				int nextbase = xcheck(c);
				base[state] = nextbase;
				int next = nextbase + key(c);
				check[next] = state;
				addAlt(state, c);
				state = next;
				taili++;
			}

			int nextbase = xcheck(tailchars[taili]);
			base[state] = nextbase;

			char tc = tailchars[taili];
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
		}

		private void addAlt(int state, char c) {
			char[] chars = alts[state];
			if (chars != null) {
				alts[state] = join(chars, c);
			} else {
				alts[state] = new char[] {c};
			}
		}

		private int xcheck(char... input) {
			int nextbase = 0;
			nextState: while (nextbase >= 0) {
				nextbase++;
				for (char c : input) {
					int next = nextbase + key(c);
					if (next >= check.length) {
						check = expand(check, next);
						base = expand(base, next);
						tail = expand(tail, next);
						alts = expand(alts, next);
						attachments = expand(attachments, next);
					}
					if (check[next] != 0) {
						continue nextState;
					}
				}
				return nextbase;
			}
			return -1;
		}

	}

	private static class AdaptorTailNode<S> implements CharNode<S>, AttachmentAdaptor<S> {

		private AdaptorNode<S> node;
		private int tailpos;
		private char[] alts;

		public AdaptorTailNode(AdaptorNode<S> node, int tailpos) {
			this.node = node;
			this.tailpos = tailpos;
			this.alts = tailpos == node.tail.length
				? new char[0]
				: new char[] {node.tail[tailpos]};
		}

		@Override
		public CharNode<S> nextNode(char c) {
			if (tailpos < node.tail.length && alts[0] == c) {
				return node.fetch(tailpos + 1);
			} else {
				return null;
			}
		}

		@Override
		public S getAttached() {
			if (node.tail.length == tailpos) {
				return node.adaptors.attachments[node.state];
			} else {
				return null;
			}
		}

		@Override
		public char[] getAlternatives() {
			return alts;
		}

		@Override
		public int getAlternativesSize() {
			return alts.length;
		}

		@Override
		public void attach(S attached) {
			if (node.tail.length == tailpos) {
				node.adaptors.attachments[node.state] = attached;
			}
			node.adaptors.attach(node.state, tailpos, attached);
		}

	}

	private static class AdaptorNode<S> implements CharNode<S>, AttachmentAdaptor<S> {

		private NodeAdaptors<S> adaptors;
		private int state;

		private char[] tail;
		private AdaptorTailNode<S>[] tailnodes;

		public AdaptorNode(NodeAdaptors<S> adaptors, int state) {
			this.adaptors = adaptors;
			this.state = state;
		}

		@Override
		public CharNode<S> nextNode(char c) {
			int statebase = adaptors.base[state];
			if (statebase < 0) {
				return fetch(0).nextNode(c);
			}
			int next = statebase + key(c);
			if (next < adaptors.check.length && adaptors.check[next] == state) {
				return adaptors.fetch(next);
			} else {
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		private AdaptorTailNode<S> fetch(int pos) {
			if (tail == null) {
				tail = adaptors.tail[state];
				if (tail == null) {
					return null;
				}
				tailnodes = new AdaptorTailNode[tail.length + 1];
			}
			if (pos > tailnodes.length) {
				return null;
			}
			AdaptorTailNode<S> node = tailnodes[pos];
			if (node == null) {
				node = new AdaptorTailNode<>(this, pos);
				tailnodes[pos] = node;
			}
			return node;
		}

		@Override
		public S getAttached() {
			return fetch(0).getAttached();
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
		public void attach(S attached) {
			adaptors.attach(state, attached);
		}

	}

	private static class Cursor<S> implements CharAutomaton<S> {

		private int[] base;
		private int[] check;
		private char[][] tail;
		private S[] attachments;
		private int state;
		private char[] activetail;
		private int tailposition;
		private AttachmentIterator iterator;

		public Cursor(int[] base, int[] check, char[][] tail, S[] attachments) {
			this.base = base;
			this.check = check;
			this.tail = tail;
			this.attachments = attachments;
			this.state = 1;
			this.activetail = null;
			this.tailposition = 0;
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
			this.activetail = null;
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
				S s = attachments[state];
				if (s != null) {
					return true;
				}
			}
			return false;
		}

		private class AttachmentIterator implements Iterator<S> {

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
			public S next() {
				if (state == 0) {
					throw new NoSuchElementException();
				}
				if (tail[state] == NO_CHARS || activetail != null && tailposition == activetail.length) {
					S a = attachments[state];
					state = 0;
					return a;
				}
				throw new NoSuchElementException();
			}
		}

	}

}
