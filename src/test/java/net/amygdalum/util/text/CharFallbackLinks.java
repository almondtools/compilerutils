package net.amygdalum.util.text;

import static java.util.Arrays.asList;
import static net.amygdalum.util.text.AttachmentAdaptor.attach;
import static net.amygdalum.util.text.CharFallbackAdaptor.getFallback;
import static net.amygdalum.util.text.CharFallbackAdaptor.setFallback;

import java.util.ArrayList;
import java.util.List;

public class CharFallbackLinks implements CharTask<String> {

	private CharNode<String> root;

	@Override
	public List<CharNode<String>> init(CharNode<String> root) {
		this.root = root;
		setFallback(root, null);
		return asList(root);
	}

	@Override
	public List<CharNode<String>> process(CharNode<String> node) {
		List<CharNode<String>> nexts = new ArrayList<>();
		for (char c : node.getAlternatives()) {
			CharNode<String> next = node.nextNode(c);
			CharNode<String> down = getFallback(node);
			nextdown: while (down != null) {
				CharNode<String> nextNode = down.nextNode(c);
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
