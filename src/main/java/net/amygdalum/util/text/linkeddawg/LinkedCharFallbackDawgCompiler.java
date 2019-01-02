package net.amygdalum.util.text.linkeddawg;

import static net.amygdalum.util.text.CharFallbackAdaptor.getFallback;
import static net.amygdalum.util.text.CharFallbackAdaptor.setFallback;

import net.amygdalum.util.text.CharDawg;
import net.amygdalum.util.text.CharWordGraphCompiler;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public class LinkedCharFallbackDawgCompiler<T> implements CharWordGraphCompiler<T, CharDawg<T>> {

	@Override
	public CharNode<T> create() {
		return new CharGenericFallbackNode<>();
	}

	@Override
	public CharDawg<T> build(CharNode<T> node) {
		return new LinkedCharFallbackDawg<>(node);
	}

	@Override
	public NodeResolver<CharNode<T>> resolver() {
		return new CharNodesCompiler<T>() {
			protected CharNode<T> compileNode(CharNode<T> node) {
				CharNode<T> optimizedNode = CharTerminalFallbackNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				optimizedNode = CharArrayFallbackNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				optimizedNode = CharMapFallbackNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				return null;
			}
			
			@Override
			public void link(CharNode<T> node) {
				CharNode<T> fallbackNode = getFallback(node);
				if (fallbackNode != null) {
					CharNode<T> compiledFallbackNode = resolve(fallbackNode);
					setFallback(node, compiledFallbackNode);
				}
			}

		};
	}

}