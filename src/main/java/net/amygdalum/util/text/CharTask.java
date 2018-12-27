package net.amygdalum.util.text;

import java.util.List;

public interface CharTask<T> {

	List<CharNode<T>> init(CharNode<T> root);

	List<CharNode<T>> process(CharNode<T> node);

}