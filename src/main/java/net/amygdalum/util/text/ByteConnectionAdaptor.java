package net.amygdalum.util.text;

public interface ByteConnectionAdaptor<T> {

	void addNextNode(byte b, ByteNode<T> next);

	@SuppressWarnings("unchecked")
	static <T> void addNextNode(Object node, byte b, ByteNode<T> next) {
		((ByteConnectionAdaptor<T>) node).addNextNode(b, next);
	}

}
