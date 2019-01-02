package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.ByteNavigator;
import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.WordSetNavigationException;

public class LinkedByteNavigator<T> implements ByteNavigator<T, LinkedByteNavigator<T>> {

	private ByteNode<T> node;

	public LinkedByteNavigator(ByteNode<T> node) {
		this.node = node;
	}

	@Override
	public LinkedByteNavigator<T> nextNode(byte b) {
		node = node.nextNode(b);
		if (node == null) {
			throw new WordSetNavigationException("unexpected navigation to " + b);
		}
		return this;
	}

	@Override
	public T getAttached() {
		return node.getAttached();
	}

}
