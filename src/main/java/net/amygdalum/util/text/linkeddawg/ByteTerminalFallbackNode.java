package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.ByteFallbackAdaptor;
import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.NodeResolver;

public class ByteTerminalFallbackNode<T> implements ByteNode<T>, ByteFallbackAdaptor<T> {

	private static final byte[] NONE = new byte[0];

	private ByteNode<T> fallbackNode;
	private T attached;

	public ByteTerminalFallbackNode(T attached) {
		this.attached = attached;
	}

	public static <T> ByteTerminalFallbackNode<T> buildNodeFrom(ByteNode<T> node, NodeResolver<ByteNode<T>> resolver) {
		if (node.getAlternativesSize() > 0) {
			return null;
		}
		ByteTerminalFallbackNode<T> buildNode = new ByteTerminalFallbackNode<>(node.getAttached());
		ByteNode<T> fallbackNode = ByteFallbackAdaptor.getFallback(node);
		if (fallbackNode != null) {
			buildNode.setFallback(fallbackNode);
		}
		return buildNode;
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

	@Override
	public void setFallback(ByteNode<T> fallbackNode) {
		this.fallbackNode = fallbackNode;
	}

	@Override
	public ByteNode<T> getFallback() {
		return fallbackNode;
	}
}
