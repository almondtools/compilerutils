package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.ByteDawg;
import net.amygdalum.util.text.ByteWordGraphCompiler;
import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.NodeResolver;

public class LinkedByteDawgCompiler<T> implements ByteWordGraphCompiler<T, ByteDawg<T>> {

	@Override
	public ByteNode<T> create() {
		return new ByteGenericNode<>();
	}

	@Override
	public ByteDawg<T> build(ByteNode<T> node) {
		return new LinkedByteDawg<>(node);
	}

	@Override
	public NodeResolver<ByteNode<T>> resolver() {
		return new ByteNodesCompiler<T>() {
			protected ByteNode<T> compileNode(ByteNode<T> node) {
				ByteNode<T> optimizedNode = ByteTerminalNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				optimizedNode = ByteArrayNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				return null;
			}

			@Override
			public void link(ByteNode<T> node) {
			}
		};
	}

}