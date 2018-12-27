package net.amygdalum.util.graph;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;


public class GraphTest {

	private Graph<String> graph;
	
	@Before
	public void before() {
		graph = new Graph<String>();
	}
	
	@Test
	public void testCreateNode() throws Exception {
		GraphNode<String> node = graph.createNode("Node");
		assertThat(node.getKey(), equalTo("Node"));
		assertThat(graph.getNode("Node"), sameInstance(node));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConnectNodes() throws Exception {
		GraphNode<String> node1 = graph.createNode("Node1");
		GraphNode<String> node2 = graph.createNode("Node2");
		graph.connectNodes("Node1", "Node2");
		assertThat(node1.getPredecessors(), emptyArray());
		assertThat(node1.getSuccessors(), arrayContaining(node2));
		assertThat(node2.getPredecessors(), arrayContaining(node1));
		assertThat(node2.getSuccessors(), emptyArray());
	}

}
