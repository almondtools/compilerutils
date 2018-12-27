package net.amygdalum.util.text.doublearraytrie;

import net.amygdalum.util.text.CharTrie;

public interface DoubleArrayCharTrie<T> extends CharTrie<T> {

	void insert(char[] chars, T out);

}
