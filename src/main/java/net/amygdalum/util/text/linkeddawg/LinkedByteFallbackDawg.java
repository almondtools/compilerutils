package net.amygdalum.util.text.linkeddawg;

import static net.amygdalum.util.text.ByteFallbackAdaptor.getFallback;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.ByteAutomaton;
import net.amygdalum.util.text.ByteDawg;
import net.amygdalum.util.text.ByteNode;

public class LinkedByteFallbackDawg<T> implements ByteDawg<T> {

	private ByteNode<T> root;

	public LinkedByteFallbackDawg(ByteNode<T> root) {
		this.root = root;
	}

	@Override
	public ByteAutomaton<T> cursor() {
		return new Cursor<>(root);
	}
	
	@Override
	public boolean contains(byte[] bytes) {
		ByteNode<T> node = root;
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			node = node.nextNode(b);
			if (node == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public T find(byte[] bytes) {
		ByteNode<T> node = root;
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			node = node.nextNode(b);
			if (node == null) {
				return null;
			}
		}
		return node.getAttached();
	}

	@Override
	public ByteNode<T> asNode() {
		return root;
	}

	private static class Cursor<S> implements ByteAutomaton<S> {

		private ByteNode<S> current;
		private ByteNode<S> root;
		private AttachmentIterator<S> iterator;

		public Cursor(ByteNode<S> root) {
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
			ByteNode<S> next = current.nextNode(b);
			if (next == null) {
				ByteNode<S> down = getFallback(current);
				while (down != null) {
					next = down.nextNode(b);
					if (next != null) {
						break;
					}
					down = getFallback(down);
				}
			}
			current = next;
			return current != null;
		}

		@Override
		public boolean hasAttachments() {
			ByteNode<S> node = current;
			while (node != null) {
				if (node.getAttached() != null) {
					return true;
				}
				node = getFallback(node);
			}
			return false;
		}

	}

	private static class AttachmentIterator<S> implements Iterator<S> {

		private ByteNode<S> current;

		public void init(ByteNode<S> current) {
			this.current = current;
		}

		@Override
		public boolean hasNext() {
			while (current != null) {
				if (current.getAttached() != null) {
					return true;
				}
				current = getFallback(current);
			}
			return false;
		}

		@Override
		public S next() {
			while (current != null) {
				S attached = current.getAttached();
				current = getFallback(current);
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
