package net.amygdalum.util.graph;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class ReversePostOrderTraversal<T extends Comparable<T>> implements Traversal<T> {

	@Override
	public void traverse(GraphNode<T> start) {
		Set<GraphNode<T>> visited = new HashSet<>();

		Deque<GraphNode<T>> ordered = new LinkedList<>();
		List<GraphNode<T>> reversePostOrdered = new LinkedList<>();

		ordered.push(start);

		while (!ordered.isEmpty()) {
			GraphNode<T> node = ordered.peek();
			if (visited.contains(node)) {
				ordered.pop();
				reversePostOrdered.add(0, node);
				continue;
			}
			visited.add(node);

			GraphNode<T>[] successors = node.getSuccessors();
			for (int i = successors.length - 1; i >= 0; i--) {
				if (visited.contains(successors[i])) {
					continue;
				}
				ordered.push(successors[i]);
			}
		}

		for (GraphNode<T> node : reversePostOrdered) {
			visitGraphNode(node);
		}
	}

}
