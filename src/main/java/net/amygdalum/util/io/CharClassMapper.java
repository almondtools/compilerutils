package net.amygdalum.util.io;

public interface CharClassMapper {

	int getIndex(char ch);

	int indexCount();

	char representative(int i);

	char representative(char ch);

	char[] representatives();

}
