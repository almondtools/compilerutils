package net.amygdalum.util.text;

public interface CharNode<T> {

	CharNode<T> nextNode(char c);
	
	T getAttached();

	int getAlternativesSize();
	
	char[] getAlternatives();
	
}
