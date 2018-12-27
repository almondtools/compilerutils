package net.amygdalum.util.text;

public interface CharConnectionAdaptor<T> {

	void addNextNode(char c, CharNode<T> next);

	@SuppressWarnings("unchecked")
	static <T> void addNextNode(Object node, char c, CharNode<T> next) {
		((CharConnectionAdaptor<T>) node).addNextNode(c, next);
	}

}
