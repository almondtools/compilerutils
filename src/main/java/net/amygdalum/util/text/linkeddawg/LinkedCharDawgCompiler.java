package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.CharDawg;
import net.amygdalum.util.text.CharWordGraphCompiler;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public class LinkedCharDawgCompiler<T> implements CharWordGraphCompiler<T, CharDawg<T>> {

	@Override
	public CharNode<T> create() {
		return new CharGenericNode<>();
	}

	@Override
	public CharDawg<T> build(CharNode<T> node) {
		return new LinkedCharDawg<>(node);
	}

	@Override
	public NodeResolver<CharNode<T>> resolver() {
		return new CharNodesCompiler<T>() {
			protected CharNode<T> compileNode(CharNode<T> node) {
				CharNode<T> optimizedNode = CharTerminalNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				optimizedNode = CharArrayNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				optimizedNode = CharMapNode.buildNodeFrom(node, this);
				if (optimizedNode != null) {
					return optimizedNode;
				}
				return null;
			}

			@Override
			public void link(CharNode<T> node) {
			}
		};
	}

}