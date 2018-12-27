package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.NodeResolver;

public class ByteTerminalNode<T> implements ByteNode<T> {

	private static final byte[] NONE = new byte[0];
	
	private T attached;

	public ByteTerminalNode(T attached) {
		this.attached = attached;
	}
	
	public static <T> ByteTerminalNode<T> buildNodeFrom(ByteNode<T> node, NodeResolver<ByteNode<T>> resolver) {
		if (node.getAlternativesSize() > 0) {
			return null;
		}
		return new ByteTerminalNode<>(node.getAttached());
	}
	
	@Override
	public ByteNode<T> nextNode(byte b) {
		return null;
	}

	@Override
	public T getAttached() {
		return attached;
	}

	@Override
	public byte[] getAlternatives() {
		return NONE;
	}
	
	@Override
	public int getAlternativesSize() {
		return 0;
	}

}
