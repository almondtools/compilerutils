package net.amygdalum.util.graph;


public interface Traversal<T extends Comparable<T>> {

	void traverse(GraphNode<T> start);

	void visitGraphNode(GraphNode<T> node);

}
