package net.amygdalum.util.text;

public interface CharWordGraphCompiler<T,R> {

	CharNode<T> create();

	R build(CharNode<T> node);

	NodeResolver<CharNode<T>> resolver();

}