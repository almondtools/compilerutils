package net.amygdalum.util.text.linkeddawg;

import static net.amygdalum.util.text.ByteFallbackAdaptor.getFallback;
import static net.amygdalum.util.text.ByteFallbackAdaptor.setFallback;

import net.amygdalum.util.text.ByteDawg;
import net.amygdalum.util.text.ByteWordGraphCompiler;
import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.NodeResolver;

public class LinkedByteFallbackDawgCompiler<T> implements ByteWordGraphCompiler<T, ByteDawg<T>> {

	@Override
	public ByteNode<T> create() {
		return new ByteGenericFallbackNode<>();
	}

	@Override
	public ByteDawg<T> build(ByteNode<T> node) {
		return new LinkedByteFallbackDawg<>(node);
	}

	@Override
	public NodeResolver<ByteNode<T>> resolver() {
		return new ByteNodesCompiler<T>() {
			protected ByteNode<T> compileNode(ByteNode<T> node) {
				ByteNode<T> optimizedNode = ByteTerminalFallbackNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				optimizedNode = ByteArrayFallbackNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				return null;
			}
			
			@Override
			public void link(ByteNode<T> node) {
				ByteNode<T> fallbackNode = getFallback(node);
				if (fallbackNode != null) {
					ByteNode<T> compiledFallbackNode = resolve(fallbackNode);
					setFallback(node, compiledFallbackNode);
				}
			}

		};
	}

}