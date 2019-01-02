package net.amygdalum.util.text.linkeddawg;

import static net.amygdalum.util.text.CharUtils.charToString;

import net.amygdalum.util.text.CharNavigator;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.WordSetNavigationException;

public class LinkedCharNavigator<T> implements CharNavigator<T, LinkedCharNavigator<T>> {

	private CharNode<T> node;

	public LinkedCharNavigator(CharNode<T> node) {
		this.node = node;
	}

	@Override
	public LinkedCharNavigator<T> nextNode(char c) {
		node = node.nextNode(c);
		if (node == null) {
			throw new WordSetNavigationException("unexpected navigation to " + charToString(c));
		}
		return this;
	}

	@Override
	public T getAttached() {
		return node.getAttached();
	}
}
