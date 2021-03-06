package net.amygdalum.util.graph;

import static net.amygdalum.util.graph.GraphSamples.create1234567;
import static net.amygdalum.util.graph.GraphSamples.createABDEC;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class PostOrderTraversalTest {

	@Test
	public void testPostOrderABDEC() throws Exception {
		Graph<String> graph = createABDEC();
		Traversal traversal = new Traversal();

		traversal.traverse(graph.getNode("A"));

		assertThat(traversal.buffer(), equalTo("EDBCA"));
	}

	@Test
	public void testPostOrder1234567() throws Exception {
		Graph<String> graph = create1234567();
		Traversal traversal = new Traversal();

		traversal.traverse(graph.getNode("1"));

		assertThat(traversal.buffer(), equalTo("4526731"));
	}

	private static class Traversal extends PostOrderTraversal<String> {
		private StringBuilder buffer = new StringBuilder();

		@Override
		public void visitGraphNode(GraphNode<String> node) {
			buffer.append(node.getKey());
		}

		public String buffer() {
			return buffer.toString();
		}
	}
}
