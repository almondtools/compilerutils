package net.amygdalum.util.text.doublearraytrie;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.amygdalum.util.text.ByteNode;
import net.amygdalum.util.text.ByteTask;
import net.amygdalum.util.text.ByteTrie;
import net.amygdalum.util.text.ByteTrieBuilder;

public class DoubleArrayByteTrieBuilder<T> implements ByteTrieBuilder<T> {

	private DoubleArrayByteTrie<T> trie;

	public DoubleArrayByteTrieBuilder(DoubleArrayByteTrie<T> trie) {
		this.trie = trie;
	}

	@Override
	public ByteTrieBuilder<T> extend(byte[] bytes, T value) {
		trie.insert(bytes, value);
		return this;
	}
	
	@Override
	public ByteTrieBuilder<T> work(ByteTask<T> task) {
		Queue<ByteNode<T>> worklist = new LinkedList<>();
		worklist.addAll(task.init(trie.asNode()));
		while (!worklist.isEmpty()) {
			ByteNode<T> current = worklist.remove();
			List<ByteNode<T>> nexts = task.process(current);
			worklist.addAll(nexts);
		}
		return this;
	}

	public ByteTrie<T> build() {
		return trie;
	}

}
