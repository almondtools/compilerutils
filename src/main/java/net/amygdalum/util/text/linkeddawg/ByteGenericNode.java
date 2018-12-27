package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.map.ByteObjectMap;
import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.ByteConnectionAdaptor;
import net.amygdalum.util.text.ByteNode;

public class ByteGenericNode<T> implements ByteNode<T>, ByteConnectionAdaptor<T>, AttachmentAdaptor<T> {

	private ByteObjectMap<ByteNode<T>> nexts;
	private T attached;

	public ByteGenericNode() {
		this.nexts = new ByteObjectMap<ByteNode<T>>(null);
	}

	@Override
	public void addNextNode(byte b, ByteNode<T> node) {
		nexts.put(b, node);
	}

	@Override
	public ByteNode<T> nextNode(byte b) {
		return nexts.get(b);
	}

	@Override
	public byte[] getAlternatives() {
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
