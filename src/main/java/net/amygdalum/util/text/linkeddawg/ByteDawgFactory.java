package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.ByteDawg;
import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.NodeResolver;

public interface ByteDawgFactory<T> {

	ByteNode<T> create();

	ByteDawg<T> build(ByteNode<T> node);

	NodeResolver<ByteNode<T>> resolver();

}