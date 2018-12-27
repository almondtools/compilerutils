package net.amygdalum.util.text;

public interface CharTrie<T> extends CharWordSet<T> {

	CharNode<T> asNode();

}
