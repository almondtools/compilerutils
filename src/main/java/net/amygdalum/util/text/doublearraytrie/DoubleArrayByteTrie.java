package net.amygdalum.util.text.doublearraytrie;

import net.amygdalum.util.text.ByteTrie;

public interface DoubleArrayByteTrie<T> extends ByteTrie<T> {

	void insert(byte[] bytes, T out);

}
