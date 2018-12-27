package net.amygdalum.util.text;

public interface CharTrieBuilder<T> {

	CharTrieBuilder<T> extend(char[] chars, T data);

	CharTrieBuilder<T> work(CharTask<T> task);

	CharTrie<T> build();

}
