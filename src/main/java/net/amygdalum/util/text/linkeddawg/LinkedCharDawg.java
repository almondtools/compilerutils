package net.amygdalum.util.text.linkeddawg;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.CharAutomaton;
import net.amygdalum.util.text.CharDawg;
import net.amygdalum.util.text.CharNode;

/**
 * A LinkedCharWordGraph is a Directed Acyclic Word Graph based on characters.
 * It has following properties: - acyclic (no back links, no support links) -
 * each node may be reached by one multiple other nodes (i.e. not a tree)
 * 
 * @param <T> the type of attachment storable in each graph node
 */
public class LinkedCharDawg<T> implements CharDawg<T> {

	private CharNode<T> root;

	public LinkedCharDawg(CharNode<T> root) {
		this.root = root;
	}

	@Override
	public CharAutomaton<T> cursor() {
		return new Cursor<>(root);
	}

	@Override
	public boolean contains(char[] chars) {
		CharNode<T> node = root;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			node = node.nextNode(c);
			if (node == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public T find(char[] chars) {
		CharNode<T> node = root;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			node = node.nextNode(c);
			if (node == null) {
				return null;
			}
		}
		return node.getAttached();
	}

	@Override
	public CharNode<T> asNode() {
		return root;
	}

	private static class Cursor<S> implements CharAutomaton<S> {

		private CharNode<S> current;
		private CharNode<S> root;
		private AttachmentIterator<S> iterator;

		public Cursor(CharNode<S> root) {
			this.root = root;
			this.current = root;
			this.iterator = new AttachmentIterator<>();
		}

		@Override
		public Iterator<S> iterator() {
			iterator.init(current);
			return iterator;
		}

		@Override
		public void reset() {
			this.current = root;
		}

		@Override
		public boolean lookahead(char c) {
			return current.nextNode(c) != null;
		}

		@Override
		public boolean accept(char c) {
			CharNode<S> next = current.nextNode(c);
			if (next == null) {
				return false;
			}
			current = next;
			return true;
		}

		@Override
		public boolean hasAttachments() {
			return current != null
				&& current.getAttached() != null;
		}

	}

	private static class AttachmentIterator<S> implements Iterator<S> {

		private CharNode<S> current;

		public void init(CharNode<S> current) {
			this.current = current;
		}

		@Override
		public boolean hasNext() {
			return current != null
				&& current.getAttached() != null;
		}

		@Override
		public S next() {
			if (current != null) {
				S attached = current.getAttached();
				current = null;
				if (attached != null) {
					return attached;
				}
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
