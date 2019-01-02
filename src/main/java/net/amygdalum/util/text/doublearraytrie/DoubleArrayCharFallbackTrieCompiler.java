package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import net.amygdalum.util.text.CharFallbackAdaptor;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.CharTrie;
import net.amygdalum.util.text.CharWordGraphCompiler;
import net.amygdalum.util.text.NodeResolver;
import net.amygdalum.util.text.linkeddawg.CharGenericFallbackNode;

public class DoubleArrayCharFallbackTrieCompiler<T> implements CharWordGraphCompiler<T, CharTrie<T>> {

	@Override
	public CharNode<T> create() {
		return new CharGenericFallbackNode<>();
	}

	@Override
	public CharTrie<T> build(CharNode<T> node) {
		DoubleArrayCharFallbackTrie.Builder<T> builder = new DoubleArrayCharFallbackTrie.Builder<T>();
		boolean[] visited = new boolean[1024];
		Map<CharNode<T>, Integer> assignments = new IdentityHashMap<>();
		Queue<Assignment<T>> todo = new LinkedList<>();
		todo.add(new Assignment<>(builder.root(), node));
		while (!todo.isEmpty()) {
			Assignment<T> current = todo.remove();
			int currentState = current.state;
			CharNode<T> currentNode = current.node;
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
		for (Map.Entry<CharNode<T>, Integer> current : assignments.entrySet()) {
			CharNode<T> currentNode = current.getKey();
			int currentState = current.getValue();
			
			CharNode<Object> fallbackNode = CharFallbackAdaptor.getFallback(currentNode);
			if (fallbackNode != null) {
				int fallbackState = assignments.getOrDefault(fallbackNode, 0);
				builder.fallback(currentState, fallbackState);
			}
		}

		return builder.build();
	}

	private void branch(DoubleArrayCharFallbackTrie.Builder<T> builder, int currentState, CharNode<T> currentNode, Queue<Assignment<T>> todo) {
		char[] alternatives = currentNode.getAlternatives();
		int[] nextStates = builder.insert(currentState, alternatives);
		for (int i = 0; i < nextStates.length; i++) {
			todo.add(new Assignment<>(nextStates[i], currentNode.nextNode(alternatives[i])));
		}
		T currentAttached = currentNode.getAttached();
		if (currentAttached != null) {
			builder.attach(currentState, currentAttached);
		}
	}
	
	private void terminate(DoubleArrayCharFallbackTrie.Builder<T> builder, int currentState, CharNode<T> currentNode) {
		builder.terminate(currentState);
		T currentAttached = currentNode.getAttached();
		if (currentAttached != null) {
			builder.attach(currentState, currentAttached);
		}
	}

	@Override
	public NodeResolver<CharNode<T>> resolver() {
		return new Resolver();
	}
	
	private static class Assignment<T> {

		public int state;
		public CharNode<T> node;
		
		public Assignment(int state, CharNode<T> node) {
			this.state = state;
			this.node = node;
		}
		
		
	}

	private class Resolver implements NodeResolver<CharNode<T>> {

		@Override
		public void compile(CharNode<T> node) {
		}

		@Override
		public void link(CharNode<T> node) {
		}

		@Override
		public CharNode<T> resolve(CharNode<T> node) {
			return node;
		}

	}

}
