package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public class CharTerminalNode<T> implements CharNode<T> {

	private static final char[] NONE = new char[0];
	
	private T attached;

	private CharTerminalNode(T attached) {
		this.attached = attached;
	}
	
	public static <T> CharTerminalNode<T> buildNodeFrom(CharNode<T> node, NodeResolver<CharNode<T>> resolver) {
		if (node.getAlternativesSize() > 0) {
			return null;
		}
		return new CharTerminalNode<>(node.getAttached());
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

}
