package net.amygdalum.util.graph;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Graph<K extends Comparable<K>> {

	private Map<K, GraphNode<K>> nodes;
	
	public Graph() {
		this.nodes = new LinkedHashMap<K, GraphNode<K>>();
	}
	
	public GraphNode<K> getNode(K key) {
		return nodes.get(key);
	}

	public GraphNode<K> createNode(K key) {
		GraphNode<K> node = nodes.get(key);
		if (node == null) {
			node = new GraphNode<K>(key);
			nodes.put(key, node);
		}
		return node;
	}
	
	public Collection<GraphNode<K>> getNodes() {
		return nodes.values();
	}

	public void connectNodes(K from, K to) {
		GraphNode<K> toNode = nodes.get(to);
		GraphNode<K> fromNode = nodes.get(from);
		fromNode.addSuccessor(toNode);
		toNode.addPredecessor(fromNode);
	}

}
