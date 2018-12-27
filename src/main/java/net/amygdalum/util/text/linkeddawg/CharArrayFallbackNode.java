package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.CharFallbackAdaptor;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public class CharArrayFallbackNode<T> implements CharNode<T>, CharFallbackAdaptor<T> {

	private static final int MAX_SIZE = 128;

	private int mask;
	private char[] chars;
	private CharNode<T>[] nodes;
	private char[] alts;
	private CharNode<T> fallbackNode;
	private T attached;

	private CharArrayFallbackNode(int mask, char[] chars, CharNode<T>[] nodes, char[] alts, T attached) {
		this.mask = mask;
		this.chars = chars;
		this.nodes = nodes;
		this.alts = alts;
		this.attached = attached;
	}

	public static <T> CharArrayFallbackNode<T> buildNodeFrom(CharNode<T> node, NodeResolver<CharNode<T>> resolver) {
		int minsize = minimumSize(node.getAlternativesSize());
		if (minsize > MAX_SIZE) {
			return null;
		}
		char[] alts = node.getAlternatives();
		int size = computeArraySize(minsize, alts);
		if (size < 0) {
			return null;
		}
		T attached = node.getAttached();
		int mask = size - 1;

		char[] chars = new char[size];
		@SuppressWarnings("unchecked")
		CharNode<T>[] nodes = new CharNode[size];
		for (char c : alts) {
			int index = ((int) c) & mask;
			chars[index] = c;
			nodes[index] = resolver.resolve(node.nextNode(c));
		}

		CharArrayFallbackNode<T> buildNode = new CharArrayFallbackNode<T>(mask, chars, nodes, alts, attached);
		CharNode<T> fallbackNode = CharFallbackAdaptor.getFallback(node);
		if (fallbackNode != null) {
			buildNode.setFallback(fallbackNode);
		}
		return buildNode;
	}

	private static <T> int computeArraySize(int minsize, char[] alternatives) {
		nextMask: for (int size = minsize; size <= MAX_SIZE; size <<= 1) {
			boolean[] collision = new boolean[size];
			int mask = size - 1;
			for (char c : alternatives) {
				int index = ((int) c) & mask;
				if (collision[index]) {
					continue nextMask;
				} else {
					collision[index] = true;
				}
			}
			return size;
		}
		return -1;
	}

	private static int minimumSize(int nextSize) {
		int minimumSize = 1;
		while (minimumSize < nextSize) {
			minimumSize <<= 1;
		}
		return minimumSize;
	}

	@Override
	public CharNode<T> nextNode(char c) {
		if (nodes.length == 0) {
			return null;
		}
		int index = ((int) c) & mask;
		if (chars[index] != c) {
			return null;
		} else {
			return nodes[index];
		}
	}

	@Override
	public char[] getAlternatives() {
		return alts;
	}

	@Override
	public int getAlternativesSize() {
		return alts.length;
	}

	@Override
	public T getAttached() {
		return attached;
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
