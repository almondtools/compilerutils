package net.amygdalum.util.text;

import java.util.List;

public interface ByteTask<T> {

	List<ByteNode<T>> init(ByteNode<T> root);

	List<ByteNode<T>> process(ByteNode<T> node);

}