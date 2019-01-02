package net.amygdalum.util.text.linkeddawg;

import static net.amygdalum.util.text.CharUtils.charToString;

import net.amygdalum.util.text.CharFallbackAdaptor;
import net.amygdalum.util.text.CharFallbackNavigator;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.WordSetNavigationException;

public class LinkedCharFallbackNavigator<T> implements CharFallbackNavigator<T, LinkedCharFallbackNavigator<T>> {

	private CharNode<T> node;

	public LinkedCharFallbackNavigator(CharNode<T> node) {
		this.node = node;
	}

	@Override
	public LinkedCharFallbackNavigator<T> nextNode(char c) {
		node = node.nextNode(c);
		if (node == null) {
			throw new WordSetNavigationException("unexpected navigation to " + charToString(c));
		}
		return this;
	}

	@Override
	public LinkedCharFallbackNavigator<T> fallback() {
		node = CharFallbackAdaptor.getFallback(node);
		return this;
	}

	@Override
	public T getAttached() {
		return node.getAttached();
	}

}
