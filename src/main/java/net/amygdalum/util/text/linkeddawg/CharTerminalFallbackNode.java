package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.CharFallbackAdaptor;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public class CharTerminalFallbackNode<T> implements CharNode<T>, CharFallbackAdaptor<T> {

	private static final char[] NONE = new char[0];

	private CharNode<T> fallbackNode;
	private T attached;

	private CharTerminalFallbackNode(T attached) {
		this.attached = attached;
	}

	public static <T> CharTerminalFallbackNode<T> buildNodeFrom(CharNode<T> node, NodeResolver<CharNode<T>> resolver) {
		if (node.getAlternativesSize() > 0) {
			return null;
		}
		CharTerminalFallbackNode<T> buildNode = new CharTerminalFallbackNode<>(node.getAttached());
		CharNode<T> fallbackNode = CharFallbackAdaptor.getFallback(node);
		if (fallbackNode != null) {
			buildNode.setFallback(fallbackNode);
		}
		return buildNode;
	}

	@Override
	public CharNode<T> nextNode(char c) {
		return null;
	}

	@Override
	public T getAttached() {
		return attached;
	}

	@Override
	public char[] getAlternatives() {
		return NONE;
	}

	@Override
	public int getAlternativesSize() {
		return 0;
	}

	@Override
	public void setFallback(CharNode<T> fallbackNode) {
		this.fallbackNode = fallbackNode;
	}

	@Override
	public CharNode<T> getFallback() {
		return fallbackNode;
	}

}
