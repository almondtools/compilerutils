package net.amygdalum.util.text;

public interface CharNavigator<T, SELF extends CharNavigator<T, ?>> {

	SELF nextNode(char c);

	T getAttached();

}
