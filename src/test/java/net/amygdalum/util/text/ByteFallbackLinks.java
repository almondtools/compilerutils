package net.amygdalum.util.text;

import static java.util.Arrays.asList;
import static net.amygdalum.util.text.AttachmentAdaptor.attach;
import static net.amygdalum.util.text.ByteFallbackAdaptor.getFallback;
import static net.amygdalum.util.text.ByteFallbackAdaptor.setFallback;

import java.util.ArrayList;
import java.util.List;

public class ByteFallbackLinks implements ByteTask<String> {

	private ByteNode<String> root;

	@Override
	public List<ByteNode<String>> init(ByteNode<String> root) {
		this.root = root;
		setFallback(root, null);
		return asList(root);
	}

	@Override
	public List<ByteNode<String>> process(ByteNode<String> node) {
		List<ByteNode<String>> nexts = new ArrayList<>();
		for (byte b : node.getAlternatives()) {
			ByteNode<String> next = node.nextNode(b);
			ByteNode<String> down = getFallback(node);
			nextdown: while (down != null) {
				ByteNode<String> nextNode = down.nextNode(b);
				if (nextNode != null) {
					setFallback(next, nextNode);
					if (next.getAttached() == null) {
						String attachment = nextNode.getAttached();
						if (attachment != null) {
							attach(next, attachment);
						}
					}
					break nextdown;
				}
				down = getFallback(down);
			}
			if (down == null) {
				setFallback(next, root);
			}
			nexts.add(next);
		}
		return nexts;
	}

}
