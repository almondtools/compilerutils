package net.amygdalum.util.text;

public interface CharFallbackNavigator<T, SELF extends CharFallbackNavigator<T, ?>> extends CharNavigator<T, SELF> {

	SELF fallback();

}
