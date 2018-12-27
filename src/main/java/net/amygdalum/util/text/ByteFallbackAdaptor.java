package net.amygdalum.util.text;

public interface ByteFallbackAdaptor<T> {

	ByteNode<T> getFallback();
	
	void setFallback(ByteNode<T> fallbackNode);
	
	@SuppressWarnings("unchecked")
	static <T> ByteNode<T> getFallback(Object node) {
		return ((ByteFallbackAdaptor<T>) node).getFallback();
	}

	@SuppressWarnings("unchecked")
	static <T> void setFallback(Object node, ByteNode<T> fallback) {
		((ByteFallbackAdaptor<T>) node).setFallback(fallback);
	}

}
