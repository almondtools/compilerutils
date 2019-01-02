package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import net.amygdalum.util.text.ByteFallbackAdaptor;
import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.ByteTrie;
import net.amygdalum.util.text.ByteWordGraphCompiler;
import net.amygdalum.util.text.NodeResolver;
import net.amygdalum.util.text.linkeddawg.ByteGenericFallbackNode;

public class DoubleArrayByteFallbackTrieCompiler<T> implements ByteWordGraphCompiler<T, ByteTrie<T>> {

	@Override
	public ByteNode<T> create() {
		return new ByteGenericFallbackNode<>();
	}

	@Override
	public ByteTrie<T> build(ByteNode<T> node) {
		DoubleArrayByteFallbackTrie.Builder<T> builder = new DoubleArrayByteFallbackTrie.Builder<T>();
		boolean[] visited = new boolean[1024];
		Map<ByteNode<T>, Integer> assignments = new IdentityHashMap<>();
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
			assignments.put(currentNode, currentState);
			
			int alternatives = currentNode.getAlternativesSize();
			if (alternatives >= 1) {
				branch(builder, currentState, currentNode, todo);
			} else {
				terminate(builder, currentState, currentNode);
			}
		}
		for (Map.Entry<ByteNode<T>, Integer> current : assignments.entrySet()) {
			ByteNode<T> currentNode = current.getKey();
			int currentState = current.getValue();
			
			ByteNode<Object> fallbackNode = ByteFallbackAdaptor.getFallback(currentNode);
			if (fallbackNode != null) {
				int fallbackState = assignments.getOrDefault(fallbackNode, 0);
				builder.fallback(currentState, fallbackState);
			}
		}

		return builder.build();
	}

	private void branch(DoubleArrayByteFallbackTrie.Builder<T> builder, int currentState, ByteNode<T> currentNode, Queue<Assignment<T>> todo) {
		byte[] alternatives = currentNode.getAlternatives();
		int[] nextStates = builder.insert(currentState, alternatives);
		for (int i = 0; i < nextStates.length; i++) {
			todo.add(new Assignment<>(nextStates[i], currentNode.nextNode(alternatives[i])));
		}
		T currentAttached = currentNode.getAttached();
		if (currentAttached != null) {
			builder.attach(currentState, currentAttached);
		}
	}
	
	private void terminate(DoubleArrayByteFallbackTrie.Builder<T> builder, int currentState, ByteNode<T> currentNode) {
		builder.terminate(currentState);
		T currentAttached = currentNode.getAttached();
		if (currentAttached != null) {
			builder.attach(currentState, currentAttached);
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
