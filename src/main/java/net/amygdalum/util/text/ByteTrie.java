package net.amygdalum.util.text;

public interface ByteTrie<T> extends ByteWordSet<T> {

	ByteNavigator<T, ?> navigator();

}
