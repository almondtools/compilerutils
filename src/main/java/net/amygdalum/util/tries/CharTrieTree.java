package net.amygdalum.util.tries;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharTrieTree<T> implements CharTrie<T> {

	private CharTrieNode<T> root;

	public CharTrieTree(CharTrieNode<T> root) {
		this.root = root;
	}

	@Override
	public CharTrieCursor<T> cursor() {
		return new Cursor<>(root);
	}
	
	private static class Cursor<S> implements CharTrieCursor<S> {

		private CharTrieNode<S> current;
		private CharTrieNode<S> root;
		private AttachmentIterator<S> iterator;

		public Cursor(CharTrieNode<S> root) {
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
			CharTrieNode<S> next = current.nextNode(c);
			while (next == null) {
				CharTrieNode<S> nextcurrent = current.getLink();
				if (nextcurrent == null) {
					break;
				}
				current = nextcurrent;
				next = current.nextNode(c);
			}
			if (next == null) {
				return false;
			}
			current = next;
			return true;
		}

		@Override
		public boolean hasAttachments() {
			CharTrieNode<S> node = current;
			while (node != null) {
				if (node.getAttached() != null) {
					return true;
				}
				node = node.getLink();
			}
			return false;
		}
		
	}

	private static class AttachmentIterator<S> implements Iterator<S> {

		private CharTrieNode<S> current;

		public void init(CharTrieNode<S> current) {
			this.current = current;
		}

		@Override
		public boolean hasNext() {
			while (current != null) {
				if (current.getAttached() != null) {
					return true;
				}
				current = current.getLink();
			}
			return false;
		}

		@Override
		public S next() {
			while (current != null) {
				S attached = current.getAttached();
				current = current.getLink();
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
