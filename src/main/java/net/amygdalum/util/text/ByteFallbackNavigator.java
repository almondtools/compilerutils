package net.amygdalum.util.text;

public interface ByteFallbackNavigator<T, SELF extends ByteFallbackNavigator<T,?>> extends ByteNavigator<T, SELF> {

	SELF fallback();

}
