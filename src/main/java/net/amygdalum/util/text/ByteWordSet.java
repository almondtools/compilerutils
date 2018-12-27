package net.amygdalum.util.text;

public interface ByteWordSet<T> {

	ByteAutomaton<T> cursor();

	boolean contains(byte[] bytes);

	T find(byte[] bytes);

}
