package net.amygdalum.util.text;

public interface CharDawgBuilder<T> {

	CharDawgBuilder<T> extend(char[] chars, T data);

	CharDawgBuilder<T> work(CharTask<T> task);

	CharDawg<T> build();
}
