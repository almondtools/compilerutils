package net.amygdalum.util.text.linkeddawg;

import java.util.IdentityHashMap;
import java.util.Map;

import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.NodeResolver;

public abstract class ByteNodesCompiler<T> implements NodeResolver<ByteNode<T>> {

	private Map<ByteNode<T>, ByteNode<T>> compiled;

	public ByteNodesCompiler() {
		this.compiled = new IdentityHashMap<>();
	}

	@Override
	public void compile(ByteNode<T> node) {
		compiled.put(node, compileNode(node));
	}

	protected abstract ByteNode<T> compileNode(ByteNode<T> node);

	@Override
	public ByteNode<T> resolve(ByteNode<T> node) {
		ByteNode<T> compiledNode = compiled.get(node);
		if (compiledNode == null) {
			return node;
		}
		return compiledNode;
	}

}