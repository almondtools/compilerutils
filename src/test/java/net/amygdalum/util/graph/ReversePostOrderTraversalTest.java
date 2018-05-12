package net.amygdalum.util.graph;

import static net.amygdalum.util.graph.GraphSamples.createGraph;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;


public class ReversePostOrderTraversalTest {

	private Graph<String> graph;
	
	@Before
	public void before() {
		graph = createGraph();
	}

	@Test
	public void testReversePostOrder() throws Exception {
		final StringBuilder buffer = new StringBuilder();
		ReversePostOrderTraversal<String, String> rpo = new ReversePostOrderTraversal<String, String>(graph) {

			@Override
			public void visitGraphNode(GraphNode<String> node) {
				buffer.append(node.getKey());
			}
		};
		rpo.traverse();
		assertThat(buffer.toString(), equalTo("ACBDE"));
	}
}
