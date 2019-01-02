package net.amygdalum.util.text;

public interface ByteWordGraphCompiler<T, R> {

	ByteNode<T> create();

	R build(ByteNode<T> node);

	NodeResolver<ByteNode<T>> resolver();

}