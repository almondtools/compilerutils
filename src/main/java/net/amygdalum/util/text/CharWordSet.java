package net.amygdalum.util.text;

public interface CharWordSet<T> {

	CharAutomaton<T> cursor();

	boolean contains(char[] chars);

	T find(char[] chars);

}
