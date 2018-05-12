package net.amygdalum.util.graph;

import static net.amygdalum.util.graph.GraphSamples.createGraph;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;


public class PostOrderTraversalTest {

	private Graph<String> graph;
	
	@Before
	public void before() {
		graph = createGraph();
	}

	@Test
	public void testPostOrder() throws Exception {
		final StringBuilder buffer = new StringBuilder();
		PostOrderTraversal<String, String> po = new PostOrderTraversal<String, String>(graph) {

			@Override
			public void visitGraphNode(GraphNode<String> node) {
				buffer.append(node.getKey());
			}
		};
		po.traverse();
		assertThat(buffer.toString(), equalTo("EDBCA"));
	}
}
