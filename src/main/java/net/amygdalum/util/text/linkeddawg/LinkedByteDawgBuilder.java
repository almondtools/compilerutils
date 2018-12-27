package net.amygdalum.util.text.linkeddawg;

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

import net.amygdalum.util.text.ByteDawg;
import net.amygdalum.util.text.ByteDawgBuilder;
import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.ByteTask;
import net.amygdalum.util.text.JoinStrategy;
import net.amygdalum.util.text.NodeResolver;

public class LinkedByteDawgBuilder<T> implements ByteDawgBuilder<T> {

	private ByteDawgFactory<T> factory;
	private JoinStrategy<T> strategy;
	private ByteNode<T> root;

	public LinkedByteDawgBuilder(ByteDawgFactory<T> factory) {
		this.factory = factory;
		this.root = factory.create();
	}

	public LinkedByteDawgBuilder(ByteDawgFactory<T> factory, JoinStrategy<T> strategy) {
		this.factory = factory;
		this.strategy = strategy;
		this.root = factory.create();
	}

	@Override
	public ByteDawgBuilder<T> extend(byte[] bytes, T data) {
		ByteNode<T> node = root;
		for (byte b : bytes) {
			ByteNode<T> next = node.nextNode(b);
			if (next == null) {
				next = factory.create();
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

	@Override
	public ByteDawgBuilder<T> work(ByteTask<T> task) {
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

	@Override
	public ByteDawg<T> build() {
		NodeResolver<ByteNode<T>> nodes = factory.resolver();
		for (ByteNode<T> node : postOrdered()) {
			nodes.compile(node);
		}
		for (ByteNode<T> node : compiled()) {
			nodes.link(node);
		}
		return factory.build(nodes.resolve(root));
	}

}
