package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.doublearraytrie.Arrays.NO_CHARS;
import static net.amygdalum.util.text.doublearraytrie.Arrays.expand;
import static net.amygdalum.util.text.doublearraytrie.Arrays.prefix;

import java.util.LinkedList;
import java.util.Queue;

import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.CharTrie;
import net.amygdalum.util.text.CharWordGraphCompiler;
import net.amygdalum.util.text.NodeResolver;
import net.amygdalum.util.text.linkeddawg.CharGenericNode;

public class DoubleArrayCharCompactTrieCompiler<T> implements CharWordGraphCompiler<T, CharTrie<T>> {

	@Override
	public CharNode<T> create() {
		return new CharGenericNode<>();
	}

	@Override
	public CharTrie<T> build(CharNode<T> node) {
		DoubleArrayCharCompactTrie.Builder<T> builder = new DoubleArrayCharCompactTrie.Builder<T>();
		boolean[] visited = new boolean[1024];
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

	private void branch(DoubleArrayCharCompactTrie.Builder<T> builder, int currentState, CharNode<T> currentNode, Queue<Assignment<T>> todo) {
		char[] alternatives = currentNode.getAlternatives();
		int[] nextStates = builder.insert(currentState, alternatives);
		for (int i = 0; i < nextStates.length; i++) {
			todo.add(new Assignment<>(nextStates[i], currentNode.nextNode(alternatives[i])));
		}
		T currentAttached = currentNode.getAttached();
		if (currentAttached != null) {
			builder.attach(currentState, NO_CHARS, currentAttached);
		}
	}

	private void sprout(DoubleArrayCharCompactTrie.Builder<T> builder, int currentState, CharNode<T> currentNode, Queue<Assignment<T>> todo) {
		char[] seq = new char[16];
		int seqpos = 0;
		while (currentNode.getAttached() == null && currentNode.getAlternativesSize() == 1) {
			char c = currentNode.getAlternatives()[0];
			if (seqpos >= seq.length) {
				seq = expand(seq, seqpos);
			}
			seq[seqpos] = c;
			currentNode = currentNode.nextNode(c);
			seqpos++;
		}
		seq = prefix(seq, seqpos);
		if (currentNode.getAlternativesSize() == 0) {
			T currentAttached = currentNode.getAttached();
			builder.attach(currentState, seq, currentAttached);
			builder.terminate(currentState);
		} else {
			for (char c : seq) {
				currentState = builder.insert(currentState, c)[0];
			}
			char[] alternatives = currentNode.getAlternatives();
			int[] nextStates = builder.insert(currentState, alternatives);
			for (int i = 0; i < nextStates.length; i++) {
				todo.add(new Assignment<>(nextStates[i], currentNode.nextNode(alternatives[i])));
			}
			T currentAttached = currentNode.getAttached();
			if (currentAttached != null) {
				builder.attach(currentState, NO_CHARS, currentAttached);
			}
		}
	}

	private void terminate(DoubleArrayCharCompactTrie.Builder<T> builder, int currentState, CharNode<T> currentNode) {
		builder.terminate(currentState);
		T currentAttached = currentNode.getAttached();
		if (currentAttached != null) {
			builder.attach(currentState, NO_CHARS, currentAttached);
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
