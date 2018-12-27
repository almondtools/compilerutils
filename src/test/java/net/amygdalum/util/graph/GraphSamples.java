package net.amygdalum.util.graph;

public class GraphSamples {

	public static Graph<String> createABDEC() {
		Graph<String> graph = new Graph<String>();
		graph.createNode("A");
		graph.createNode("B");
		graph.createNode("C");
		graph.createNode("D");
		graph.createNode("E");
		graph.connectNodes("A", "B");
		graph.connectNodes("A", "C");
		graph.connectNodes("B", "D");
		graph.connectNodes("C", "D");
		graph.connectNodes("D", "E");
		return graph;
	}
	
	public static Graph<String> create1234567() {
		Graph<String> graph = new Graph<String>();
		graph.createNode("1");
		graph.createNode("2");
		graph.createNode("3");
		graph.createNode("4");
		graph.createNode("5");
		graph.createNode("6");
		graph.createNode("7");
		graph.connectNodes("1", "2");
		graph.connectNodes("1", "3");
		graph.connectNodes("2", "4");
		graph.connectNodes("2", "5");
		graph.connectNodes("3", "6");
		graph.connectNodes("3", "7");
		return graph;
	}
}
