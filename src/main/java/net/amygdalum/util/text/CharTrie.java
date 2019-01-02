package net.amygdalum.util.text;

public interface CharTrie<T> extends CharWordSet<T> {

	CharNavigator<T, ?> navigator();

}
