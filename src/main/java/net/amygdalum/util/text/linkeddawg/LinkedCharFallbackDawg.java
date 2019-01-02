package net.amygdalum.util.text.linkeddawg;

import static net.amygdalum.util.text.CharFallbackAdaptor.getFallback;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.amygdalum.util.text.CharAutomaton;
import net.amygdalum.util.text.CharDawg;
import net.amygdalum.util.text.CharFallbackNavigator;
import net.amygdalum.util.text.CharNode;

public class LinkedCharFallbackDawg<T> implements CharDawg<T> {

	private CharNode<T> root;

	public LinkedCharFallbackDawg(CharNode<T> root) {
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
	public CharFallbackNavigator<T, ?> navigator() {
		return new LinkedCharFallbackNavigator<>(root);
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
				CharNode<S> down = getFallback(current);
				while (down != null) {
					next = down.nextNode(c);
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
			CharNode<S> node = current;
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

		private CharNode<S> current;

		public void init(CharNode<S> current) {
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
