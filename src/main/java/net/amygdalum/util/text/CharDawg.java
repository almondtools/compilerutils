package net.amygdalum.util.text;

public interface CharDawg<T> extends CharWordSet<T> {

	CharNavigator<T, ?> navigator();

}
