package net.amygdalum.util.text;

import static net.amygdalum.util.text.AttachmentAdaptor.attach;
import static net.amygdalum.util.text.ByteConnectionAdaptor.addNextNode;

import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

public class ByteWordSetBuilder<T, R> {

	private ByteWordGraphCompiler<T, R> compiler;
	private JoinStrategy<T> strategy;
	private ByteNode<T> root;

	public ByteWordSetBuilder(ByteWordGraphCompiler<T, R> compiler) {
		this.compiler = compiler;
		this.root = compiler.create();
	}

	public ByteWordSetBuilder(ByteWordGraphCompiler<T, R> compiler, JoinStrategy<T> strategy) {
		this.compiler = compiler;
		this.strategy = strategy;
		this.root = compiler.create();
	}

	public ByteWordSetBuilder<T, R> extend(byte[] bytes, T data) {
		ByteNode<T> node = root;
		for (byte b : bytes) {
			ByteNode<T> next = node.nextNode(b);
			if (next == null) {
				next = compiler.create();
				addNextNode(node, b, next);
			}
			node = next;
		}
		if (data == null) {
			return this;
		}
		if (strategy != null) {
			T existing = node.getAttached();
			T joinedData = strategy.join(existing, data);
			if (joinedData != existing) {
				attach(node, joinedData);
			}
		} else {
			attach(node, data);
		}
		return this;
	}

	public ByteWordSetBuilder<T, R> work(ByteTask<T> task) {
		Queue<ByteNode<T>> worklist = new LinkedList<>();
		worklist.addAll(task.init(root));
		while (!worklist.isEmpty()) {
			ByteNode<T> current = worklist.remove();
			List<ByteNode<T>> nexts = task.process(current);
			worklist.addAll(nexts);
		}
		return this;
	}

	private Queue<ByteNode<T>> postOrdered() {
		Map<ByteNode<T>, int[]> counters = new IdentityHashMap<>();

		Queue<ByteNode<T>> todo = new LinkedList<>();
		todo.add(root);
		counters.put(root, new int[1]);

		int max = 1;
		while (!todo.isEmpty()) {
			ByteNode<T> current = todo.remove();
			for (byte b : current.getAlternatives()) {
				ByteNode<T> nextNode = current.nextNode(b);
				int[] counter = counters.get(nextNode);
				if (counter == null) {
					counter = new int[1];
					counters.put(nextNode, counter);
					todo.add(nextNode);
				}
				counter[0]++;
				if (max < counter[0]) {
					max = counter[0];
				}
			}
		}

		Queue<ByteNode<T>> nexts = new LinkedList<>();
		Iterator<Entry<ByteNode<T>, int[]>> iterator = counters.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ByteNode<T>, int[]> entry = iterator.next();
			if (entry.getValue()[0] == 0) {
				nexts.add(entry.getKey());
				iterator.remove();
			}
		}

		Deque<ByteNode<T>> postOrdered = new LinkedList<>();

		while (!counters.isEmpty()) {
			if (nexts.isEmpty()) {
				throw new IllegalArgumentException("graph is not acylic");
			}
			while (!nexts.isEmpty()) {
				ByteNode<T> next = nexts.remove();
				postOrdered.push(next);
				for (byte b : next.getAlternatives()) {
					ByteNode<T> ref = next.nextNode(b);
					int[] counter = counters.get(ref);
					if (--counter[0] == 0) {
						nexts.add(ref);
						counters.remove(ref);
					}
				}

			}
		}

		return postOrdered;
	}

	private List<ByteNode<T>> compiled() {
		Set<ByteNode<T>> visited = new HashSet<>();
		List<ByteNode<T>> compiled = new LinkedList<>();

		Queue<ByteNode<T>> todo = new LinkedList<>();
		todo.add(root);
		while (!todo.isEmpty()) {
			ByteNode<T> current = todo.remove();
			if (visited.contains(current)) {
				continue;
			}
			visited.add(current);
			compiled.add(current);
			for (byte b : current.getAlternatives()) {
				ByteNode<T> nextNode = current.nextNode(b);
				todo.add(nextNode);
			}
		}

		return compiled;
	}

	public R build() {
		NodeResolver<ByteNode<T>> nodes = compiler.resolver();
		for (ByteNode<T> node : postOrdered()) {
			nodes.compile(node);
		}
		for (ByteNode<T> node : compiled()) {
			nodes.link(node);
		}
		return compiler.build(nodes.resolve(root));
	}

}
