package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.doublearraytrie.Arrays.NO_BYTES;
import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;
import static net.amygdalum.util.text.doublearraytrie.Arrays.prefix;

import java.util.LinkedList;
import java.util.Queue;

import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.ByteTrie;
import net.amygdalum.util.text.ByteWordGraphCompiler;
import net.amygdalum.util.text.NodeResolver;
import net.amygdalum.util.text.linkeddawg.ByteGenericNode;

public class DoubleArrayByteCompactTrieCompiler<T> implements ByteWordGraphCompiler<T, ByteTrie<T>> {

	@Override
	public ByteNode<T> create() {
		return new ByteGenericNode<>();
	}

	@Override
	public ByteTrie<T> build(ByteNode<T> node) {
		DoubleArrayByteCompactTrie.Builder<T> builder = new DoubleArrayByteCompactTrie.Builder<T>();
		boolean[] visited = new boolean[1024];
		Queue<Assignment<T>> todo = new LinkedList<>();
		todo.add(new Assignment<>(builder.root(), node));
		while (!todo.isEmpty()) {
			Assignment<T> current = todo.remove();
			int currentState = current.state;
			ByteNode<T> currentNode = current.node;
			if (currentState >= visited.length) {
				visited = expand(visited, currentState);
			}
			if (visited[currentState]) {
				continue;
			}
			visited[currentState] = true;

			int alternatives = currentNode.getAlternativesSize();
			if (alternatives > 1) {
				branch(builder, currentState, currentNode, todo);
			} else if (alternatives == 1) {
				sprout(builder, currentState, currentNode, todo);
			} else {
				terminate(builder, currentState, currentNode);
			}

		}

		return builder.build();
	}

	private void branch(DoubleArrayByteCompactTrie.Builder<T> builder, int currentState, ByteNode<T> currentNode, Queue<Assignment<T>> todo) {
		byte[] alternatives = currentNode.getAlternatives();
		int[] nextStates = builder.insert(currentState, alternatives);
		for (int i = 0; i < nextStates.length; i++) {
			todo.add(new Assignment<>(nextStates[i], currentNode.nextNode(alternatives[i])));
		}
		T currentAttached = currentNode.getAttached();
		if (currentAttached != null) {
			builder.attach(currentState, NO_BYTES, currentAttached);
		}
	}

	private void sprout(DoubleArrayByteCompactTrie.Builder<T> builder, int currentState, ByteNode<T> currentNode, Queue<Assignment<T>> todo) {
		byte[] seq = new byte[16];
		int seqpos = 0;
		while (currentNode.getAttached() == null && currentNode.getAlternativesSize() == 1) {
			byte b = currentNode.getAlternatives()[0];
			if (seqpos >= seq.length) {
				seq = expand(seq, seqpos);
			}
			seq[seqpos] = b;
			currentNode = currentNode.nextNode(b);
			seqpos++;
		}
		seq = prefix(seq, seqpos);
		if (currentNode.getAlternativesSize() == 0) {
			T currentAttached = currentNode.getAttached();
			builder.attach(currentState, seq, currentAttached);
			builder.terminate(currentState);
		} else {
			for (byte b : seq) {
				currentState = builder.insert(currentState, b)[0];
			}
			byte[] alternatives = currentNode.getAlternatives();
			int[] nextStates = builder.insert(currentState, alternatives);
			for (int i = 0; i < nextStates.length; i++) {
				todo.add(new Assignment<>(nextStates[i], currentNode.nextNode(alternatives[i])));
			}
			T currentAttached = currentNode.getAttached();
			if (currentAttached != null) {
				builder.attach(currentState, NO_BYTES, currentAttached);
			}
		}
	}

	private void terminate(DoubleArrayByteCompactTrie.Builder<T> builder, int currentState, ByteNode<T> currentNode) {
		builder.terminate(currentState);
		T currentAttached = currentNode.getAttached();
		if (currentAttached != null) {
			builder.attach(currentState, NO_BYTES, currentAttached);
		}
	}

	@Override
	public NodeResolver<ByteNode<T>> resolver() {
		return new Resolver();
	}

	private static class Assignment<T> {

		public int state;
		public ByteNode<T> node;

		public Assignment(int state, ByteNode<T> node) {
			this.state = state;
			this.node = node;
		}

	}

	private class Resolver implements NodeResolver<ByteNode<T>> {

		@Override
		public void compile(ByteNode<T> node) {
		}

		@Override
		public void link(ByteNode<T> node) {
		}

		@Override
		public ByteNode<T> resolve(ByteNode<T> node) {
			return node;
		}

	}

}
