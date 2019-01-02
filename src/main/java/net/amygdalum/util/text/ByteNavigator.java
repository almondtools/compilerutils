package net.amygdalum.util.text;

public interface ByteNavigator<T, SELF extends ByteNavigator<T, ?>> {

	SELF nextNode(byte b);

	T getAttached();

}
