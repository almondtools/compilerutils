package net.amygdalum.util.text;

public interface ByteTrieBuilder<T> {

	ByteTrieBuilder<T> extend(byte[] bytes, T data);

	ByteTrieBuilder<T> work(ByteTask<T> task);

	ByteWordSet<T> build();

}
