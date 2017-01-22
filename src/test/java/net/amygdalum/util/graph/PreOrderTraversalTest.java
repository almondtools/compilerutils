package net.amygdalum.util.graph;

import static net.amygdalum.util.graph.GraphSamples.createGraph;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.util.graph.Graph;
import net.amygdalum.util.graph.GraphNode;
import net.amygdalum.util.graph.PreOrderTraversal;


public class PreOrderTraversalTest {

	private Graph<String> graph;
	
	@Before
	public void before() {
		graph = createGraph();
	}

	@Test
	public void testPreOrder() throws Exception {
		final StringBuilder buffer = new StringBuilder();
		PreOrderTraversal<String, String> po = new PreOrderTraversal<String, String>(graph) {

			@Override
			public void visitGraphNode(GraphNode<String> node) {
				buffer.append(node.getKey());
			}
		};
		po.traverse();
		assertThat(buffer.toString(), equalTo("ABDEC"));
	}
}
