package net.amygdalum.util.text;

public interface CharFallbackAdaptor<T> {

	CharNode<T> getFallback();

	void setFallback(CharNode<T> fallbackNode);

	@SuppressWarnings("unchecked")
	static <T> CharNode<T> getFallback(Object node) {
		return ((CharFallbackAdaptor<T>) node).getFallback();
	}

	@SuppressWarnings("unchecked")
	static <T> void setFallback(Object node, CharNode<T> fallback) {
		((CharFallbackAdaptor<T>) node).setFallback(fallback);
	}

}
