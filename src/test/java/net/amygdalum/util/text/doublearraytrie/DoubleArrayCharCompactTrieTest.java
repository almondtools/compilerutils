package net.amygdalum.util.text.doublearraytrie;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.util.text.AttachmentAdaptor;
import net.amygdalum.util.text.doublearraytrie.DoubleArrayCharCompactTrie;

public class DoubleArrayCharCompactTrieTest {

	private DoubleArrayCharCompactTrie<String> trie;

	@Before
	public void before() throws Exception {
		trie = new DoubleArrayCharCompactTrie<>();
	}

	@Test
	public void testSingleNode() throws Exception {
		trie.insert("bachelor".toCharArray(), "Bachelor");

		assertThat(trie.contains("bachelor".toCharArray()), is(true));
		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.contains("jar".toCharArray()), is(false));
		assertThat(trie.contains("badge".toCharArray()), is(false));
		assertThat(trie.contains("baby".toCharArray()), is(false));
	}

	@Test
	public void testMultipleNonCollidingNodes() throws Exception {
		trie.insert("bachelor".toCharArray(), "Bachelor");
		trie.insert("jar".toCharArray(), "Jar");

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.contains("jar".toCharArray()), is(true));
		assertThat(trie.find("jar".toCharArray()), equalTo("Jar"));
	}

	@Test
	public void testMultipleCollidingNodes() throws Exception {
		trie.insert("bachelor".toCharArray(), "Bachelor");
		trie.insert("jar".toCharArray(), "Jar");
		trie.insert("badge".toCharArray(), "Badge");

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.find("jar".toCharArray()), equalTo("Jar"));
		assertThat(trie.contains("badge".toCharArray()), is(true));
		assertThat(trie.find("badge".toCharArray()), equalTo("Badge"));
	}

	@Test
	public void testMultipleMoreCollidingNodes() throws Exception {
		trie.insert("bachelor".toCharArray(), "Bachelor");
		trie.insert("jar".toCharArray(), "Jar");
		trie.insert("badge".toCharArray(), "Badge");
		trie.insert("baby".toCharArray(), "Baby");

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.find("jar".toCharArray()), equalTo("Jar"));
		assertThat(trie.find("badge".toCharArray()), equalTo("Badge"));
		assertThat(trie.contains("baby".toCharArray()), is(true));
		assertThat(trie.find("baby".toCharArray()), equalTo("Baby"));
	}

	@Test
	public void testMultipleSubsumingNodes() throws Exception {
		trie.insert("bac".toCharArray(), "Bac");
		trie.insert("bachelor".toCharArray(), "Bachelor");

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.find("bac".toCharArray()), equalTo("Bac"));
	}

	@Test
	public void testMultipleSubsumedNodes() throws Exception {
		trie.insert("bachelor".toCharArray(), "Bachelor");
		trie.insert("bac".toCharArray(), "Bac");

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.find("bac".toCharArray()), equalTo("Bac"));
	}

	@Test
	public void testMultipleSubsumedNodes2() throws Exception {
		trie.insert("abcd".toCharArray(), "ABCD");
		trie.insert("ab".toCharArray(), "AB");
		trie.insert("bc".toCharArray(), "BC");
		trie.insert("cd".toCharArray(), "CD");

		assertThat(trie.find("abcd".toCharArray()), equalTo("ABCD"));
		assertThat(trie.find("ab".toCharArray()), equalTo("AB"));
		assertThat(trie.find("bc".toCharArray()), equalTo("BC"));
		assertThat(trie.find("cd".toCharArray()), equalTo("CD"));
	}

	@Test
	public void testMultipleSubsumedNodes3() throws Exception {
		trie.insert("aaa".toCharArray(), "AAA");
		trie.insert("aa".toCharArray(), "AA");
		trie.insert("a".toCharArray(), "A");

		assertThat(trie.find("aaa".toCharArray()), equalTo("AAA"));
		assertThat(trie.find("aa".toCharArray()), equalTo("AA"));
		assertThat(trie.find("a".toCharArray()), equalTo("A"));
	}

	@Test
	public void testAttachments() throws Exception {
		trie.insert("abc".toCharArray(), "ABC");
		trie.insert("bcd".toCharArray(), "BCD");

		assertThat(trie.find("abc".toCharArray()), equalTo("ABC"));
		assertThat(trie.find("bcd".toCharArray()), equalTo("BCD"));
		assertThat(trie.find("a".toCharArray()), nullValue());
		assertThat(trie.find("b".toCharArray()), nullValue());
		assertThat(trie.find("c".toCharArray()), nullValue());
		assertThat(trie.find("d".toCharArray()), nullValue());
	}

	@Test
	public void testAttachments2() throws Exception {
		trie.insert("".toCharArray(), "");
		trie.insert("a".toCharArray(), "A");
		trie.insert("b".toCharArray(), "B");

		assertThat(trie.find("".toCharArray()), equalTo(""));
		assertThat(trie.find("a".toCharArray()), equalTo("A"));
		assertThat(trie.find("b".toCharArray()), equalTo("B"));
		assertThat(trie.find("c".toCharArray()), nullValue());
	}

	@Test
	public void testAttachments3() throws Exception {
		trie.insert("ab".toCharArray(), "AB");
		trie.insert("aa".toCharArray(), "AA");

		assertThat(trie.find("ab".toCharArray()), equalTo("AB"));
		assertThat(trie.find("aa".toCharArray()), equalTo("AA"));
		assertThat(trie.find("bb".toCharArray()), nullValue());
	}

	@Test
	public void testAttachments4() throws Exception {
		trie.insert("bb".toCharArray(), "BB");
		trie.insert("ba".toCharArray(), "BA");
		trie.insert("bbc".toCharArray(), "BBC");
		trie.insert("bbd".toCharArray(), "BBD");
		trie.insert("bbf".toCharArray(), "BBF");
		trie.insert("bbg".toCharArray(), "BBG");
		trie.insert("bba".toCharArray(), "BBA");

		assertThat(trie.find("bb".toCharArray()), equalTo("BB"));
		assertThat(trie.find("ba".toCharArray()), equalTo("BA"));
		assertThat(trie.find("bbc".toCharArray()), equalTo("BBC"));
		assertThat(trie.find("bbd".toCharArray()), equalTo("BBD"));
		assertThat(trie.find("bbf".toCharArray()), equalTo("BBF"));
		assertThat(trie.find("bbg".toCharArray()), equalTo("BBG"));
		assertThat(trie.find("bba".toCharArray()), equalTo("BBA"));
	}

	@Test
	public void testAttachments5() throws Exception {
		trie.insert("cc".toCharArray(), "CC");
		trie.insert("ca".toCharArray(), "CA");
		trie.insert("ac".toCharArray(), "AC");
		trie.insert("aa".toCharArray(), "AA");

		assertThat(trie.find("cc".toCharArray()), equalTo("CC"));
		assertThat(trie.find("ca".toCharArray()), equalTo("CA"));
		assertThat(trie.find("ac".toCharArray()), equalTo("AC"));
		assertThat(trie.find("aa".toCharArray()), equalTo("AA"));
	}

	@Test
	public void testLargeCharacterSpace() throws Exception {
		trie.insert("\u9999".toCharArray(), "U9999");
		trie.insert("\u0000".toCharArray(), "U0000");
		
		assertThat(trie.find("\u9999".toCharArray()), equalTo("U9999"));
		assertThat(trie.find("\u0000".toCharArray()), equalTo("U0000"));
	}
	
	@Test
	public void testDoubleSubsumedNodes() throws Exception {
		trie.insert(new StringBuilder("and wood to fire").reverse().toString().toCharArray(), "and wood to fire");
		trie.insert(new StringBuilder("Then shalt thou enquire").reverse().toString().toCharArray(), "Then shalt thou enquire");
		trie.insert(new StringBuilder("fire").reverse().toString().toCharArray(), "fire");

		assertThat(trie.find(new StringBuilder("and wood to fire").reverse().toString().toCharArray()), equalTo("and wood to fire"));
		assertThat(trie.find(new StringBuilder("Then shalt thou enquire").reverse().toString().toCharArray()), equalTo("Then shalt thou enquire"));
		assertThat(trie.find(new StringBuilder("fire").reverse().toString().toCharArray()), equalTo("fire"));
	}
		
	@Test
	public void testStrings() throws Exception {
		String a = (char) 0 + "a";
		String b = (char) 0 + "b";
		trie.insert(a.toCharArray(), "A");
		trie.insert(b.toCharArray(), "B");

		assertThat(trie.find(a.toCharArray()), equalTo("A"));
		assertThat(trie.find(b.toCharArray()), equalTo("B"));
	}

	@Test
	public void testAsNodeSingleNode() throws Exception {
		char[] bachelor = "bachelor".toCharArray();
		trie.insert(bachelor, "Bachelor");

		assertThat(trie.asNode()
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
		char[] bachelor = "bachelor".toCharArray();
		trie.insert(bachelor, "Bachelor");
		char[] jar = "jar".toCharArray();
		trie.insert(jar, "Jar");

		assertThat(trie.asNode()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
		assertThat(trie.asNode()
			.nextNode(jar[0])
			.nextNode(jar[1])
			.nextNode(jar[2])
			.getAttached(), equalTo("Jar"));
	}

	@Test
	public void testAsNodeMultipleCollidingNodes() throws Exception {
		char[] bachelor = "bachelor".toCharArray();
		trie.insert(bachelor, "Bachelor");
		char[] jar = "jar".toCharArray();
		trie.insert(jar, "Jar");
		char[] badge = "badge".toCharArray();
		trie.insert(badge, "Badge");

		assertThat(trie.asNode()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
		assertThat(trie.asNode()
			.nextNode(jar[0])
			.nextNode(jar[1])
			.nextNode(jar[2])
			.getAttached(), equalTo("Jar"));
		assertThat(trie.asNode()
			.nextNode(badge[0])
			.nextNode(badge[1])
			.nextNode(badge[2])
			.nextNode(badge[3])
			.nextNode(badge[4])
			.getAttached(), equalTo("Badge"));
	}

	@Test
	public void testAsNodeMultipleMoreCollidingNodes() throws Exception {
		char[] bachelor = "bachelor".toCharArray();
		trie.insert(bachelor, "Bachelor");
		char[] jar = "jar".toCharArray();
		trie.insert(jar, "Jar");
		char[] badge = "badge".toCharArray();
		trie.insert(badge, "Badge");
		char[] baby = "baby".toCharArray();
		trie.insert(baby, "Baby");

		assertThat(trie.asNode()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
		assertThat(trie.asNode()
			.nextNode(jar[0])
			.nextNode(jar[1])
			.nextNode(jar[2])
			.getAttached(), equalTo("Jar"));
		assertThat(trie.asNode()
			.nextNode(badge[0])
			.nextNode(badge[1])
			.nextNode(badge[2])
			.nextNode(badge[3])
			.nextNode(badge[4])
			.getAttached(), equalTo("Badge"));
		assertThat(trie.asNode()
			.nextNode(baby[0])
			.nextNode(baby[1])
			.nextNode(baby[2])
			.nextNode(baby[3])
			.getAttached(), equalTo("Baby"));
	}

	@Test
	public void testAsNodeMultipleSubsumedNodes() throws Exception {
		char[] bachelor = "bachelor".toCharArray();
		trie.insert(bachelor, "Bachelor");
		char[] bac = "bac".toCharArray();
		trie.insert(bac, "Bac");

		assertThat(trie.asNode()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
		assertThat(trie.asNode()
			.nextNode(bac[0])
			.nextNode(bac[1])
			.nextNode(bac[2])
			.getAttached(), equalTo("Bac"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAsNodeMultipleReattachments() throws Exception {
		char[] bachelor = "bachelor".toCharArray();
		trie.insert(bachelor, "Bachelor");
		char[] bac = "bac".toCharArray();
		trie.insert(bac, "Bac");

		((AttachmentAdaptor<String>) trie.asNode()
		.nextNode(bachelor[0])
		.nextNode(bachelor[1])
		.nextNode(bachelor[2])
		.nextNode(bachelor[3])
		.nextNode(bachelor[4]))
		.attach("Bache");

		((AttachmentAdaptor<String>) trie.asNode()
		.nextNode(bachelor[0])
		.nextNode(bachelor[1]))
		.attach("Ba");

		assertThat(trie.asNode()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.nextNode(bachelor[5])
			.nextNode(bachelor[6])
			.nextNode(bachelor[7])
			.getAttached(), equalTo("Bachelor"));
		assertThat(trie.asNode()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.getAttached(), equalTo("Bache"));
		assertThat(trie.asNode()
			.nextNode(bac[0])
			.nextNode(bac[1])
			.nextNode(bac[2])
			.getAttached(), equalTo("Bac"));
		assertThat(trie.asNode()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.getAttached(), equalTo("Ba"));
	}

}
