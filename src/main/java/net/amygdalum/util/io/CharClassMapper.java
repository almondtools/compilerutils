package net.amygdalum.util.io;

import java.util.List;

public interface CharClassMapper {

	int getIndex(char ch);

	int indexCount();

	char representative(int i);

	char representative(char ch);

	String representatives(List<Integer> path);

	char[] representatives();

}
