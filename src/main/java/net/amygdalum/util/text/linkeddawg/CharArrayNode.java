package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public class CharArrayNode<T> implements CharNode<T> {

	private static final int MAX_SIZE = 128;

	private int mask;
	private char[] chars;
	private CharNode<T>[] nodes;
	private char[] alts;
	private T attached;

	private CharArrayNode(int mask, char[] chars, CharNode<T>[] nodes, char[] alts, T attached) {
		this.mask = mask;
		this.chars = chars;
		this.nodes = nodes;
		this.alts = alts;
		this.attached = attached;
	}

	public static <T> CharArrayNode<T> buildNodeFrom(CharNode<T> node, NodeResolver<CharNode<T>> resolver) {
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

		return new CharArrayNode<T>(mask, chars, nodes, alts, attached);
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

}
