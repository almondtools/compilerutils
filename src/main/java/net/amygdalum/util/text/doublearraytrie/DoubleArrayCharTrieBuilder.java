package net.amygdalum.util.text.doublearraytrie;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.amygdalum.util.text.CharNode;
import net.amygdalum.util.text.CharTask;
import net.amygdalum.util.text.CharTrie;
import net.amygdalum.util.text.CharTrieBuilder;

public class DoubleArrayCharTrieBuilder<T> implements CharTrieBuilder<T> {

	private DoubleArrayCharTrie<T> trie;

	public DoubleArrayCharTrieBuilder(DoubleArrayCharTrie<T> trie) {
		this.trie = trie;
	}

	@Override
	public CharTrieBuilder<T> extend(char[] chars, T value) {
		trie.insert(chars, value);
		return this;
	}
	
	@Override
	public CharTrieBuilder<T> work(CharTask<T> task) {
		Queue<CharNode<T>> worklist = new LinkedList<>();
		worklist.addAll(task.init(trie.asNode()));
		while (!worklist.isEmpty()) {
			CharNode<T> current = worklist.remove();
			List<CharNode<T>> nexts = task.process(current);
			worklist.addAll(nexts);
		}
		return this;
	}

	public CharTrie<T> build() {
		return trie;
	}

}
