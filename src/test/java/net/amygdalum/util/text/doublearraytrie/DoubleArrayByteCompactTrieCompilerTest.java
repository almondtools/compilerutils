package net.amygdalum.util.text.doublearraytrie;

import static net.amygdalum.util.text.ByteUtils.revert;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.util.text.ByteTrie;
import net.amygdalum.util.text.ByteWordSetBuilder;

public class DoubleArrayByteCompactTrieCompilerTest {

	private ByteWordSetBuilder<String, ByteTrie<String>> builder;

	@Before
	public void before() throws Exception {
		builder = new ByteWordSetBuilder<>(new DoubleArrayByteCompactTrieCompiler<String>());
	}

	@Test
	public void testSingleNode() throws Exception {
		ByteTrie<String> trie = builder
			.extend("bachelor".getBytes("UTF-8"), "Bachelor")
			.build();

		assertThat(trie.contains("bachelor".getBytes("UTF-8")), is(true));
		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.contains("jar".getBytes("UTF-8")), is(false));
		assertThat(trie.contains("badge".getBytes("UTF-8")), is(false));
		assertThat(trie.contains("baby".getBytes("UTF-8")), is(false));
	}

	@Test
	public void testMultipleNonCollidingNodes() throws Exception {
		ByteTrie<String> trie = builder
			.extend("bachelor".getBytes("UTF-8"), "Bachelor")
			.extend("jar".getBytes("UTF-8"), "Jar")
			.build();

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.contains("jar".getBytes("UTF-8")), is(true));
		assertThat(trie.find("jar".getBytes("UTF-8")), equalTo("Jar"));
	}

	@Test
	public void testMultipleCollidingNodes() throws Exception {
		ByteTrie<String> trie = builder
			.extend("bachelor".getBytes("UTF-8"), "Bachelor")
			.extend("jar".getBytes("UTF-8"), "Jar")
			.extend("badge".getBytes("UTF-8"), "Badge")
			.build();

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.find("jar".getBytes("UTF-8")), equalTo("Jar"));
		assertThat(trie.contains("badge".getBytes("UTF-8")), is(true));
		assertThat(trie.find("badge".getBytes("UTF-8")), equalTo("Badge"));
	}

	@Test
	public void testMultipleMoreCollidingNodes() throws Exception {
		ByteTrie<String> trie = builder
			.extend("bachelor".getBytes("UTF-8"), "Bachelor")
			.extend("jar".getBytes("UTF-8"), "Jar")
			.extend("badge".getBytes("UTF-8"), "Badge")
			.extend("baby".getBytes("UTF-8"), "Baby")
			.build();

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.find("jar".getBytes("UTF-8")), equalTo("Jar"));
		assertThat(trie.find("badge".getBytes("UTF-8")), equalTo("Badge"));
		assertThat(trie.contains("baby".getBytes("UTF-8")), is(true));
		assertThat(trie.find("baby".getBytes("UTF-8")), equalTo("Baby"));
	}

	@Test
	public void testMultipleSubsumingNodes() throws Exception {
		ByteTrie<String> trie = builder
			.extend("bac".getBytes("UTF-8"), "Bac")
			.extend("bachelor".getBytes("UTF-8"), "Bachelor")
			.build();

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.find("bac".getBytes("UTF-8")), equalTo("Bac"));
	}

	@Test
	public void testMultipleSubsumedNodes() throws Exception {
		ByteTrie<String> trie = builder
			.extend("bachelor".getBytes("UTF-8"), "Bachelor")
			.extend("bac".getBytes("UTF-8"), "Bac")
			.build();

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.find("bac".getBytes("UTF-8")), equalTo("Bac"));
	}

	@Test
	public void testMultipleSubsumedNodes2() throws Exception {
		ByteTrie<String> trie = builder
			.extend("abcd".getBytes("UTF-8"), "ABCD")
			.extend("ab".getBytes("UTF-8"), "AB")
			.extend("bc".getBytes("UTF-8"), "BC")
			.extend("cd".getBytes("UTF-8"), "CD")
			.build();

		assertThat(trie.find("abcd".getBytes("UTF-8")), equalTo("ABCD"));
		assertThat(trie.find("ab".getBytes("UTF-8")), equalTo("AB"));
		assertThat(trie.find("bc".getBytes("UTF-8")), equalTo("BC"));
		assertThat(trie.find("cd".getBytes("UTF-8")), equalTo("CD"));
	}

	@Test
	public void testMultipleSubsumedNodes3() throws Exception {
		ByteTrie<String> trie = builder
			.extend("aaa".getBytes("UTF-8"), "AAA")
			.extend("aa".getBytes("UTF-8"), "AA")
			.extend("a".getBytes("UTF-8"), "A")
			.build();

		assertThat(trie.find("aaa".getBytes("UTF-8")), equalTo("AAA"));
		assertThat(trie.find("aa".getBytes("UTF-8")), equalTo("AA"));
		assertThat(trie.find("a".getBytes("UTF-8")), equalTo("A"));
	}

	@Test
	public void testAttachments() throws Exception {
		ByteTrie<String> trie = builder
			.extend("abc".getBytes("UTF-8"), "ABC")
			.extend("bcd".getBytes("UTF-8"), "BCD")
			.build();

		assertThat(trie.find("abc".getBytes("UTF-8")), equalTo("ABC"));
		assertThat(trie.find("bcd".getBytes("UTF-8")), equalTo("BCD"));
		assertThat(trie.find("a".getBytes("UTF-8")), nullValue());
		assertThat(trie.find("b".getBytes("UTF-8")), nullValue());
		assertThat(trie.find("c".getBytes("UTF-8")), nullValue());
		assertThat(trie.find("d".getBytes("UTF-8")), nullValue());
	}

	@Test
	public void testAttachments2() throws Exception {
		ByteTrie<String> trie = builder
			.extend("".getBytes("UTF-8"), "")
			.extend("a".getBytes("UTF-8"), "A")
			.extend("b".getBytes("UTF-8"), "B")
			.build();

		assertThat(trie.find("".getBytes("UTF-8")), equalTo(""));
		assertThat(trie.find("a".getBytes("UTF-8")), equalTo("A"));
		assertThat(trie.find("b".getBytes("UTF-8")), equalTo("B"));
		assertThat(trie.find("c".getBytes("UTF-8")), nullValue());
	}

	@Test
	public void testAttachments3() throws Exception {
		ByteTrie<String> trie = builder
			.extend("ab".getBytes("UTF-8"), "AB")
			.extend("aa".getBytes("UTF-8"), "AA")
			.build();

		assertThat(trie.find("ab".getBytes("UTF-8")), equalTo("AB"));
		assertThat(trie.find("aa".getBytes("UTF-8")), equalTo("AA"));
		assertThat(trie.find("bb".getBytes("UTF-8")), nullValue());
	}

	@Test
	public void testAttachments4() throws Exception {
		ByteTrie<String> trie = builder
			.extend("bb".getBytes("UTF-8"), "BB")
			.extend("ba".getBytes("UTF-8"), "BA")
			.extend("bbc".getBytes("UTF-8"), "BBC")
			.extend("bbd".getBytes("UTF-8"), "BBD")
			.extend("bbf".getBytes("UTF-8"), "BBF")
			.extend("bbg".getBytes("UTF-8"), "BBG")
			.extend("bba".getBytes("UTF-8"), "BBA")
			.build();

		assertThat(trie.find("bb".getBytes("UTF-8")), equalTo("BB"));
		assertThat(trie.find("ba".getBytes("UTF-8")), equalTo("BA"));
		assertThat(trie.find("bbc".getBytes("UTF-8")), equalTo("BBC"));
		assertThat(trie.find("bbd".getBytes("UTF-8")), equalTo("BBD"));
		assertThat(trie.find("bbf".getBytes("UTF-8")), equalTo("BBF"));
		assertThat(trie.find("bbg".getBytes("UTF-8")), equalTo("BBG"));
		assertThat(trie.find("bba".getBytes("UTF-8")), equalTo("BBA"));
	}

	@Test
	public void testAttachments5() throws Exception {
		ByteTrie<String> trie = builder
			.extend("cc".getBytes("UTF-8"), "CC")
			.extend("ca".getBytes("UTF-8"), "CA")
			.extend("ac".getBytes("UTF-8"), "AC")
			.extend("aa".getBytes("UTF-8"), "AA")
			.build();

		assertThat(trie.find("cc".getBytes("UTF-8")), equalTo("CC"));
		assertThat(trie.find("ca".getBytes("UTF-8")), equalTo("CA"));
		assertThat(trie.find("ac".getBytes("UTF-8")), equalTo("AC"));
		assertThat(trie.find("aa".getBytes("UTF-8")), equalTo("AA"));
	}

	@Test
	public void testLargeByteacterSpace() throws Exception {
		ByteTrie<String> trie = builder
			.extend("\u9999".getBytes("UTF-8"), "U9999")
			.extend("\u0000".getBytes("UTF-8"), "U0000")
			.build();

		assertThat(trie.find("\u9999".getBytes("UTF-8")), equalTo("U9999"));
		assertThat(trie.find("\u0000".getBytes("UTF-8")), equalTo("U0000"));
	}

	@Test
	public void testDoubleSubsumedNodes() throws Exception {
		ByteTrie<String> trie = builder
			.extend(new StringBuilder("and wood to fire").reverse().toString().getBytes("UTF-8"), "and wood to fire")
			.extend(new StringBuilder("Then shalt thou enquire").reverse().toString().getBytes("UTF-8"), "Then shalt thou enquire")
			.extend(new StringBuilder("fire").reverse().toString().getBytes("UTF-8"), "fire")
			.build();

		assertThat(trie.find(new StringBuilder("and wood to fire").reverse().toString().getBytes("UTF-8")), equalTo("and wood to fire"));
		assertThat(trie.find(new StringBuilder("Then shalt thou enquire").reverse().toString().getBytes("UTF-8")), equalTo("Then shalt thou enquire"));
		assertThat(trie.find(new StringBuilder("fire").reverse().toString().getBytes("UTF-8")), equalTo("fire"));
	}

	@Test
	public void testStrings() throws Exception {
		String a = (char) 0 + "a";
		String b = (char) 0 + "b";
		ByteTrie<String> trie = builder
			.extend(a.getBytes("UTF-8"), "A")
			.extend(b.getBytes("UTF-8"), "B")
			.build();

		assertThat(trie.find(a.getBytes("UTF-8")), equalTo("A"));
		assertThat(trie.find(b.getBytes("UTF-8")), equalTo("B"));
	}

	@Test
	public void testAsNodeSingleNode() throws Exception {
		byte[] bachelor = "bachelor".getBytes("UTF-8");
		ByteTrie<String> trie = builder
			.extend(bachelor, "Bachelor")
			.build();

		assertThat(trie.navigator()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
	}

	@Test
	public void testAsNodeMutlipleNonCollidingNodes() throws Exception {
		byte[] bachelor = "bachelor".getBytes("UTF-8");
		byte[] jar = "jar".getBytes("UTF-8");
		ByteTrie<String> trie = builder
			.extend(bachelor, "Bachelor")
			.extend(jar, "Jar")
			.build();

		assertThat(trie.navigator()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
		assertThat(trie.navigator()
			.nextNode(jar[0])
			.nextNode(jar[1])
			.nextNode(jar[2])
			.getAttached(), equalTo("Jar"));
	}

	@Test
	public void testAsNodeMultipleCollidingNodes() throws Exception {
		byte[] bachelor = "bachelor".getBytes("UTF-8");
		byte[] jar = "jar".getBytes("UTF-8");
		byte[] badge = "badge".getBytes("UTF-8");
		ByteTrie<String> trie = builder
			.extend(bachelor, "Bachelor")
			.extend(jar, "Jar")
			.extend(badge, "Badge")
			.build();

		assertThat(trie.navigator()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
		assertThat(trie.navigator()
			.nextNode(jar[0])
			.nextNode(jar[1])
			.nextNode(jar[2])
			.getAttached(), equalTo("Jar"));
		assertThat(trie.navigator()
			.nextNode(badge[0])
			.nextNode(badge[1])
			.nextNode(badge[2])
			.nextNode(badge[3])
			.nextNode(badge[4])
			.getAttached(), equalTo("Badge"));
	}

	@Test
	public void testAsNodeMultipleMoreCollidingNodes() throws Exception {
		byte[] bachelor = "bachelor".getBytes("UTF-8");
		byte[] jar = "jar".getBytes("UTF-8");
		byte[] badge = "badge".getBytes("UTF-8");
		byte[] baby = "baby".getBytes("UTF-8");
		ByteTrie<String> trie = builder
			.extend(bachelor, "Bachelor")
			.extend(jar, "Jar")
			.extend(badge, "Badge")
			.extend(baby, "Baby")
			.build();

		assertThat(trie.navigator()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
		assertThat(trie.navigator()
			.nextNode(jar[0])
			.nextNode(jar[1])
			.nextNode(jar[2])
			.getAttached(), equalTo("Jar"));
		assertThat(trie.navigator()
			.nextNode(badge[0])
			.nextNode(badge[1])
			.nextNode(badge[2])
			.nextNode(badge[3])
			.nextNode(badge[4])
			.getAttached(), equalTo("Badge"));
		assertThat(trie.navigator()
			.nextNode(baby[0])
			.nextNode(baby[1])
			.nextNode(baby[2])
			.nextNode(baby[3])
			.getAttached(), equalTo("Baby"));
	}

	@Test
	public void testAsNodeMultipleSubsumedNodes() throws Exception {
		byte[] bachelor = "bachelor".getBytes("UTF-8");
		byte[] bac = "bac".getBytes("UTF-8");
		ByteTrie<String> trie = builder
			.extend(bachelor, "Bachelor")
			.extend(bac, "Bac")
			.build();

		assertThat(trie.navigator()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
		assertThat(trie.navigator()
			.nextNode(bac[0])
			.nextNode(bac[1])
			.nextNode(bac[2])
			.getAttached(), equalTo("Bac"));
	}

	@Test
	public void testReversedStrings() throws Exception {
		ByteTrie<String> trie = builder
			.extend(revert("And God called the firmament Heaven".getBytes("UTF-8")), "Heaven")
			.extend(revert("Let the waters under the heaven be gathered together unto one place".getBytes("UTF-8")), "Water")
			.extend(revert("And God called the dry land Earth".getBytes("UTF-8")), "Earth")
			.build();
		
		assertThat(trie.find(revert("And God called the firmament Heaven".getBytes("UTF-8"))), equalTo("Heaven"));
		assertThat(trie.find(revert("Let the waters under the heaven be gathered together unto one place".getBytes("UTF-8"))), equalTo("Water"));
		assertThat(trie.find(revert("And God called the dry land Earth".getBytes("UTF-8"))), equalTo("Earth"));
	}

}
