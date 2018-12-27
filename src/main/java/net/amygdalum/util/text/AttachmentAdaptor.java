package net.amygdalum.util.text;

public interface AttachmentAdaptor<T> {

	void attach(T attached);

	@SuppressWarnings("unchecked")
	static <T> void attach(Object node, T attachment) {
		((AttachmentAdaptor<T>) node).attach(attachment);
	}

}
