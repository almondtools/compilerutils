package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.ByteFallbackAdaptor;
import net.amygdalum.util.text.ByteFallbackNavigator;
import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.WordSetNavigationException;

public class LinkedByteFallbackNavigator<T> implements ByteFallbackNavigator<T, LinkedByteFallbackNavigator<T>> {

	private ByteNode<T> node;

	public LinkedByteFallbackNavigator(ByteNode<T> node) {
		this.node = node;
	}

	@Override
	public LinkedByteFallbackNavigator<T> nextNode(byte b) {
		node = node.nextNode(b);
		if (node == null) {
			throw new WordSetNavigationException("unexpected navigation to " + b);
		}
		return this;
	}

	@Override
	public LinkedByteFallbackNavigator<T> fallback() {
		node = ByteFallbackAdaptor.getFallback(node);
		return this;
	}

	@Override
	public T getAttached() {
		return node.getAttached();
	}

}
