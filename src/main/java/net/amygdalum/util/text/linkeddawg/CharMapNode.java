package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.map.CharObjectMap;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public class CharMapNode<T>  implements CharNode<T> {

	private CharObjectMap<CharNode<T>> nexts;
	private char[] alts;
	private T attached;
	
	private CharMapNode(CharObjectMap<CharNode<T>> nexts, char[] alts, T attached) {
		this.nexts = nexts;
		this.alts = alts;
		this.attached = attached;
	}

	public static <T> CharMapNode<T> buildNodeFrom(CharNode<T> node, NodeResolver<CharNode<T>> resolver) {
		char[] alts = node.getAlternatives();
		CharObjectMap<CharNode<T>> nexts = new CharObjectMap<CharNode<T>>(null);
		for (char c : alts) {
			nexts.add(c, resolver.resolve(node.nextNode(c)));
		}
		T attached = node.getAttached();
		return new CharMapNode<>(nexts, alts, attached);
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

}
