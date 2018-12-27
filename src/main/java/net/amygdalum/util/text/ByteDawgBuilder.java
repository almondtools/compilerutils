package net.amygdalum.util.text;

public interface ByteDawgBuilder<T> {

	ByteDawgBuilder<T> extend(byte[] bytes, T data);

	ByteDawgBuilder<T> work(ByteTask<T> task);

	ByteDawg<T> build();
}
