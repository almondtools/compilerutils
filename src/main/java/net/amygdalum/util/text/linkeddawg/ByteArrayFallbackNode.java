package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.map.ByteObjectMap;
import net.amygdalum.util.map.ByteObjectMap.Entry;
import net.amygdalum.util.text.ByteFallbackAdaptor;
import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.NodeResolver;

public class ByteArrayFallbackNode<T> implements ByteNode<T>, ByteFallbackAdaptor<T> {

	private static final int MAX_SIZE = 256;

	private int mask;
	private byte[] bytes;
	private ByteNode<T>[] nodes;
	private byte[] alts;
	private ByteNode<T> fallbackNode;
	private T attached;


	public ByteArrayFallbackNode(int mask, byte[] bytes, ByteNode<T>[] nodes, byte[] alts, T attached) {
		this.mask = mask;
		this.bytes = bytes;
		this.nodes = nodes;
		this.alts = alts;
		this.attached = attached;
	}

	public static <T> ByteArrayFallbackNode<T> buildNodeFrom(ByteNode<T> node, NodeResolver<ByteNode<T>> resolver) {
		int minsize = minimumSize(node.getAlternativesSize());
		if (minsize > MAX_SIZE) {
			return null;
		}
		byte[] alts = node.getAlternatives();
		int size = computeArraySize(minsize, alts);
		if (size < 0) {
			return null;
		}
		T attached = node.getAttached();
		int mask = size - 1;

		byte[] bytes = new byte[size];
		@SuppressWarnings("unchecked")
		ByteNode<T>[] nodes = new ByteNode[size];
		for (byte b : alts) {
			int index = ((int) b) & mask;
			bytes[index] = b;
			nodes[index] = resolver.resolve(node.nextNode(b));
		}

		ByteArrayFallbackNode<T> buildNode = new ByteArrayFallbackNode<T>(mask, bytes, nodes, alts, attached);
		ByteNode<T> fallbackNode = ByteFallbackAdaptor.getFallback(node);
		if (fallbackNode != null) {
			buildNode.setFallback(fallbackNode);
		}
		return buildNode;
	}

	private static <T> int computeArraySize(int minsize, byte[] alternatives) {
		nextMask: for (int size = minsize; size <= MAX_SIZE; size <<= 1) {
			boolean[] collision = new boolean[size];
			int mask = size - 1;
			for (byte b : alternatives) {
				int index = ((int) b) & mask;
				if (collision[index]) {
					continue nextMask;
				} else {
					collision[index] = true;
				}
			}
			return size;
		}
		return MAX_SIZE;
	}

	private static int minimumSize(int nextSize) {
		int minimumSize = 1;
		while (minimumSize < nextSize) {
			minimumSize <<= 1;
		}
		return minimumSize;
	}

	public static <T> int computeArraySize(ByteObjectMap<ByteNode<T>> nexts) {
		int nextSize = nexts.size();
		int minimumSize = 1;
		while (minimumSize < nextSize) {
			minimumSize <<= 1;
		}
		nextMask: for (int size = minimumSize; size < 256; size <<= 1) {
			boolean[] collision = new boolean[size];
			int mask = size - 1;
			for (Entry<ByteNode<T>> entry : nexts.cursor()) {
				int index = ((int) entry.key) & mask;
				if (collision[index]) {
					continue nextMask;
				} else {
					collision[index] = true;
				}
			}
			return size;
		}
		return 256;
	}

	@Override
	public ByteNode<T> nextNode(byte b) {
		if (nodes.length == 0) {
			return null;
		}
		int index = ((int) b) & mask;
		if (bytes[index] != b) {
			return null;
		} else {
			return nodes[index];
		}
	}

	@Override
	public byte[] getAlternatives() {
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
	public void setFallback(ByteNode<T> fallbackNode) {
		this.fallbackNode = fallbackNode;
	}
	
	@Override
	public ByteNode<T> getFallback() {
		return fallbackNode;
	}
}
