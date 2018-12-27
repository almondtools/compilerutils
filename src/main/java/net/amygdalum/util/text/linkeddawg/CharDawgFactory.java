package net.amygdalum.util.text.linkeddawg;

import net.amygdalum.util.text.CharDawg;
import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.NodeResolver;

public interface CharDawgFactory<T> {

	CharNode<T> create();

	CharDawg<T> build(CharNode<T> node);

	NodeResolver<CharNode<T>> resolver();

}