package net.amygdalum.util.text.linkeddawg;

import java.util.IdentityHashMap;
import java.util.Map;

import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public abstract class CharNodesCompiler<T> implements NodeResolver<CharNode<T>> {

	private Map<CharNode<T>, CharNode<T>> compiled;

	public CharNodesCompiler() {
		this.compiled = new IdentityHashMap<>();
	}

	@Override
	public void compile(CharNode<T> node) {
		compiled.put(node, compileNode(node));
	}

	protected abstract CharNode<T> compileNode(CharNode<T> node);

	@Override
	public CharNode<T> resolve(CharNode<T> node) {
		CharNode<T> compiledNode = compiled.get(node);
		if (compiledNode == null) {
			return node;
		}
		return compiledNode;
	}

}