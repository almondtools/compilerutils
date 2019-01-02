package net.amygdalum.util.text;

public interface ByteDawg<T> extends ByteWordSet<T> {

	ByteNavigator<T, ?> navigator();

}
