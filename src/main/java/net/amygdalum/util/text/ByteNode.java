package net.amygdalum.util.text;

public interface ByteNode<T> {

	ByteNode<T> nextNode(byte b);
	
	T getAttached();

	byte[] getAlternatives();

	int getAlternativesSize();
	
}
