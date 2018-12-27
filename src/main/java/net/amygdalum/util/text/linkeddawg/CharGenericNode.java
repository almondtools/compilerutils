package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.map.CharObjectMap;
import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.CharConnectionAdaptor;
import net.amygdalum.util.text.CharNode;

public class CharGenericNode<T> implements CharNode<T>, CharConnectionAdaptor<T>, AttachmentAdaptor<T> {

	private CharObjectMap<CharNode<T>> nexts;
	private T attached;

	public CharGenericNode() {
		this.nexts = new CharObjectMap<CharNode<T>>(null);
	}
	
	@Override
	public void addNextNode(char c, CharNode<T> node) {
		nexts.put(c, node);
	}

	@Override
	public CharNode<T> nextNode(char c) {
		return nexts.get(c);
	}

	@Override
	public char[] getAlternatives() {
		return nexts.keys();
	}
	
	@Override
	public int getAlternativesSize() {
		return nexts.size();
	}

	@Override
	public void attach(T attached) {
		this.attached = attached;
	}

	@Override
	public T getAttached() {
		return attached;
	}

}
