package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.map.CharObjectMap;
import net.amygdalum.util.text.CharFallbackAdaptor;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public class CharMapFallbackNode<T>  implements CharNode<T>, CharFallbackAdaptor<T> {

	private CharObjectMap<CharNode<T>> nexts;
	private char[] alts;
	private CharNode<T> fallbackNode;
	private T attached;
	
	private CharMapFallbackNode(CharObjectMap<CharNode<T>> nexts, char[] alts, T attached) {
		this.nexts = nexts;
		this.alts = alts;
		this.attached = attached;
	}

	public static <T> CharMapFallbackNode<T> buildNodeFrom(CharNode<T> node, NodeResolver<CharNode<T>> resolver) {
		char[] alts = node.getAlternatives();
		CharObjectMap<CharNode<T>> nexts = new CharObjectMap<CharNode<T>>(null);
		for (char c : alts) {
			nexts.add(c, resolver.resolve(node.nextNode(c)));
		}
		T attached = node.getAttached();
		CharMapFallbackNode<T> buildNode = new CharMapFallbackNode<>(nexts, alts, attached);
		CharNode<T> fallbackNode = CharFallbackAdaptor.getFallback(node);
		if (fallbackNode != null) {
			buildNode.setFallback(fallbackNode);
		}
		return buildNode;
	}

	@Override
	public CharNode<T> nextNode(char c) {
		return nexts.get(c);
	}

	@Override
	public char[] getAlternatives() {
		return alts;
	}
	
	@Override
	public int getAlternativesSize() {
		return alts.length;
	}
	
	@Override
	public T getAttached() {
		return attached;
	}

	@Override
	public void setFallback(CharNode<T> fallbackNode) {
		this.fallbackNode = fallbackNode;
	}
	
	@Override
	public CharNode<T> getFallback() {
		return fallbackNode;
	}

}
