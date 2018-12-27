package net.amygdalum.util.text;

public interface JoinStrategy<T> {

	T join(T existing,T next);

}