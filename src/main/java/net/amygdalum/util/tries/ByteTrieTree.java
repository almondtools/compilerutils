package net.amygdalum.util.tries;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteTrieTree<T> implements ByteTrie<T> {

	private ByteTrieNode<T> root;

	public ByteTrieTree(ByteTrieNode<T> root) {
		this.root = root;
	}

	@Override
	public ByteTrieCursor<T> cursor() {
		return new Cursor<>(root);
	}
	
	private static class Cursor<S> implements ByteTrieCursor<S> {

		private ByteTrieNode<S> current;
		private ByteTrieNode<S> root;
		private AttachmentIterator<S> iterator;

		public Cursor(ByteTrieNode<S> root) {
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
		public boolean lookahead(byte b) {
			return current.nextNode(b) != null;
		}

		@Override
		public boolean accept(byte b) {
			ByteTrieNode<S> next = current.nextNode(b);
			while (next == null) {
				ByteTrieNode<S> nextcurrent = current.getLink();
				if (nextcurrent == null) {
					break;
				}
				current = nextcurrent;
				next = current.nextNode(b);
			}
			if (next == null) {
				return false;
			}
			current = next;
			return true;
		}

		@Override
		public boolean hasAttachments() {
			ByteTrieNode<S> node = current;
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

		private ByteTrieNode<S> current;

		public void init(ByteTrieNode<S> current) {
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
