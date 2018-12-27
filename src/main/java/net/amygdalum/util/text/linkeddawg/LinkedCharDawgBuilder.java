package net.amygdalum.util.text.linkeddawg;

import static net.amygdalum.util.text.AttachmentAdaptor.attach;
import static net.amygdalum.util.text.CharConnectionAdaptor.addNextNode;

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

import net.amygdalum.util.text.CharDawg;
import net.amygdalum.util.text.CharDawgBuilder;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.CharTask;
import net.amygdalum.util.text.JoinStrategy;
import net.amygdalum.util.text.NodeResolver;

public class LinkedCharDawgBuilder<T> implements CharDawgBuilder<T> {

	private CharDawgFactory<T> factory;
	private JoinStrategy<T> strategy;
	private CharNode<T> root;

	public LinkedCharDawgBuilder(CharDawgFactory<T> factory) {
		this.factory = factory;
		this.root = factory.create();
	}

	public LinkedCharDawgBuilder(CharDawgFactory<T> factory, JoinStrategy<T> strategy) {
		this.factory = factory;
		this.strategy = strategy;
		this.root = factory.create();
	}

	@Override
	public CharDawgBuilder<T> extend(char[] chars, T data) {
		CharNode<T> node = root;
		for (char c : chars) {
			CharNode<T> next = node.nextNode(c);
			if (next == null) {
				next = factory.create();
				addNextNode(node, c, next);
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
	public CharDawgBuilder<T> work(CharTask<T> task) {
		Queue<CharNode<T>> worklist = new LinkedList<>();
		worklist.addAll(task.init(root));
		while (!worklist.isEmpty()) {
			CharNode<T> current = worklist.remove();
			List<CharNode<T>> nexts = task.process(current);
			worklist.addAll(nexts);
		}
		return this;
	}

	private Queue<CharNode<T>> postOrdered() {
		Map<CharNode<T>, int[]> counters = new IdentityHashMap<>();

		Queue<CharNode<T>> todo = new LinkedList<>();
		todo.add(root);
		counters.put(root, new int[1]);

		int max = 1;
		while (!todo.isEmpty()) {
			CharNode<T> current = todo.remove();
			for (char c : current.getAlternatives()) {
				CharNode<T> nextNode = current.nextNode(c);
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

		Queue<CharNode<T>> nexts = new LinkedList<>();
		Iterator<Entry<CharNode<T>, int[]>> iterator = counters.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<CharNode<T>, int[]> entry = iterator.next();
			if (entry.getValue()[0] == 0) {
				nexts.add(entry.getKey());
				iterator.remove();
			}
		}

		Deque<CharNode<T>> postOrdered = new LinkedList<>();

		while (!counters.isEmpty()) {
			if (nexts.isEmpty()) {
				throw new IllegalArgumentException("graph is not acylic");
			}
			while (!nexts.isEmpty()) {
				CharNode<T> next = nexts.remove();
				postOrdered.push(next);
				for (char c : next.getAlternatives()) {
					CharNode<T> ref = (CharNode<T>) next.nextNode(c);
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

	private List<CharNode<T>> compiled() {
		Set<CharNode<T>> visited = new HashSet<>();
		List<CharNode<T>> compiled = new LinkedList<>();
		
		Queue<CharNode<T>> todo = new LinkedList<>();
		todo.add(root);
		while (!todo.isEmpty()) {
			CharNode<T> current = todo.remove();
			if (visited.contains(current)) {
				continue;
			}
			visited.add(current);
			compiled.add(current);
			for (char c : current.getAlternatives()) {
				CharNode<T> nextNode = current.nextNode(c);
				todo.add(nextNode);
			}			
		}
		
		return compiled;
	}

	@Override
	public CharDawg<T> build() {
		NodeResolver<CharNode<T>> nodes = factory.resolver();
		for (CharNode<T> node : postOrdered()) {
			nodes.compile(node);
		}
		for (CharNode<T> node : compiled()) {
			nodes.link(node);
		}
		return factory.build(nodes.resolve(root));
	}

}
