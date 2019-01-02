package net.amygdalum.util.text.doublearraytrie;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.util.text.AttachmentAdaptor;

public class DoubleArrayByteFallbackTrieTest {

	private DoubleArrayByteFallbackTrie<String> trie;

	@Before
	public void before() throws Exception {
		trie = new DoubleArrayByteFallbackTrie<>();
	}

	@Test
	public void testSingleNode() throws Exception {
		trie.insert("bachelor".getBytes("UTF-8"), "Bachelor");

		assertThat(trie.contains("bachelor".getBytes("UTF-8")), is(true));
		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.contains("jar".getBytes("UTF-8")), is(false));
		assertThat(trie.contains("badge".getBytes("UTF-8")), is(false));
		assertThat(trie.contains("baby".getBytes("UTF-8")), is(false));
	}

	@Test
	public void testMultipleNonCollidingNodes() throws Exception {
		trie.insert("bachelor".getBytes("UTF-8"), "Bachelor");
		trie.insert("jar".getBytes("UTF-8"), "Jar");

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.contains("jar".getBytes("UTF-8")), is(true));
		assertThat(trie.find("jar".getBytes("UTF-8")), equalTo("Jar"));
	}

	@Test
	public void testMultipleCollidingNodes() throws Exception {
		trie.insert("bachelor".getBytes("UTF-8"), "Bachelor");
		trie.insert("jar".getBytes("UTF-8"), "Jar");
		trie.insert("badge".getBytes("UTF-8"), "Badge");

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.find("jar".getBytes("UTF-8")), equalTo("Jar"));
		assertThat(trie.contains("badge".getBytes("UTF-8")), is(true));
		assertThat(trie.find("badge".getBytes("UTF-8")), equalTo("Badge"));
	}

	@Test
	public void testMultipleMoreCollidingNodes() throws Exception {
		trie.insert("bachelor".getBytes("UTF-8"), "Bachelor");
		trie.insert("jar".getBytes("UTF-8"), "Jar");
		trie.insert("badge".getBytes("UTF-8"), "Badge");
		trie.insert("baby".getBytes("UTF-8"), "Baby");

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.find("jar".getBytes("UTF-8")), equalTo("Jar"));
		assertThat(trie.find("badge".getBytes("UTF-8")), equalTo("Badge"));
		assertThat(trie.contains("baby".getBytes("UTF-8")), is(true));
		assertThat(trie.find("baby".getBytes("UTF-8")), equalTo("Baby"));
	}

	@Test
	public void testMultipleSubsumingNodes() throws Exception {
		trie.insert("bac".getBytes("UTF-8"), "Bac");
		trie.insert("bachelor".getBytes("UTF-8"), "Bachelor");

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.find("bac".getBytes("UTF-8")), equalTo("Bac"));
	}

	@Test
	public void testMultipleSubsumedNodes() throws Exception {
		trie.insert("bachelor".getBytes("UTF-8"), "Bachelor");
		trie.insert("bac".getBytes("UTF-8"), "Bac");

		assertThat(trie.find("bachelor".getBytes("UTF-8")), equalTo("Bachelor"));
		assertThat(trie.find("bac".getBytes("UTF-8")), equalTo("Bac"));
	}

	@Test
	public void testMultipleSubsumedNodes2() throws Exception {
		trie.insert("abcd".getBytes("UTF-8"), "ABCD");
		trie.insert("ab".getBytes("UTF-8"), "AB");
		trie.insert("bc".getBytes("UTF-8"), "BC");
		trie.insert("cd".getBytes("UTF-8"), "CD");

		assertThat(trie.find("abcd".getBytes("UTF-8")), equalTo("ABCD"));
		assertThat(trie.find("ab".getBytes("UTF-8")), equalTo("AB"));
		assertThat(trie.find("bc".getBytes("UTF-8")), equalTo("BC"));
		assertThat(trie.find("cd".getBytes("UTF-8")), equalTo("CD"));
	}

	@Test
	public void testMultipleSubsumedNodes3() throws Exception {
		trie.insert("aaa".getBytes("UTF-8"), "AAA");
		trie.insert("aa".getBytes("UTF-8"), "AA");
		trie.insert("a".getBytes("UTF-8"), "A");

		assertThat(trie.find("aaa".getBytes("UTF-8")), equalTo("AAA"));
		assertThat(trie.find("aa".getBytes("UTF-8")), equalTo("AA"));
		assertThat(trie.find("a".getBytes("UTF-8")), equalTo("A"));
	}

	@Test
	public void testAttachments() throws Exception {
		trie.insert("abc".getBytes("UTF-8"), "ABC");
		trie.insert("bcd".getBytes("UTF-8"), "BCD");

		assertThat(trie.find("abc".getBytes("UTF-8")), equalTo("ABC"));
		assertThat(trie.find("bcd".getBytes("UTF-8")), equalTo("BCD"));
		assertThat(trie.find("a".getBytes("UTF-8")), nullValue());
		assertThat(trie.find("b".getBytes("UTF-8")), nullValue());
		assertThat(trie.find("c".getBytes("UTF-8")), nullValue());
		assertThat(trie.find("d".getBytes("UTF-8")), nullValue());
	}

	@Test
	public void testAttachments2() throws Exception {
		trie.insert("".getBytes("UTF-8"), "");
		trie.insert("a".getBytes("UTF-8"), "A");
		trie.insert("b".getBytes("UTF-8"), "B");

		assertThat(trie.find("".getBytes("UTF-8")), equalTo(""));
		assertThat(trie.find("a".getBytes("UTF-8")), equalTo("A"));
		assertThat(trie.find("b".getBytes("UTF-8")), equalTo("B"));
		assertThat(trie.find("c".getBytes("UTF-8")), nullValue());
	}

	@Test
	public void testAttachments3() throws Exception {
		trie.insert("ab".getBytes("UTF-8"), "AB");
		trie.insert("aa".getBytes("UTF-8"), "AA");

		assertThat(trie.find("ab".getBytes("UTF-8")), equalTo("AB"));
		assertThat(trie.find("aa".getBytes("UTF-8")), equalTo("AA"));
		assertThat(trie.find("bb".getBytes("UTF-8")), nullValue());
	}

	@Test
	public void testAttachments4() throws Exception {
		trie.insert("bb".getBytes("UTF-8"), "BB");
		trie.insert("ba".getBytes("UTF-8"), "BA");
		trie.insert("bbc".getBytes("UTF-8"), "BBC");
		trie.insert("bbd".getBytes("UTF-8"), "BBD");
		trie.insert("bbf".getBytes("UTF-8"), "BBF");
		trie.insert("bbg".getBytes("UTF-8"), "BBG");
		trie.insert("bba".getBytes("UTF-8"), "BBA");

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
		trie.insert("cc".getBytes("UTF-8"), "CC");
		trie.insert("ca".getBytes("UTF-8"), "CA");
		trie.insert("ac".getBytes("UTF-8"), "AC");
		trie.insert("aa".getBytes("UTF-8"), "AA");

		assertThat(trie.find("cc".getBytes("UTF-8")), equalTo("CC"));
		assertThat(trie.find("ca".getBytes("UTF-8")), equalTo("CA"));
		assertThat(trie.find("ac".getBytes("UTF-8")), equalTo("AC"));
		assertThat(trie.find("aa".getBytes("UTF-8")), equalTo("AA"));
	}

	@Test
	public void testLargeCharacterSpace() throws Exception {
		for (int i = 0; i < 1024; i++) {
			char c = (char) i;
			String s = String.valueOf(c);
			trie.insert(s.getBytes("UTF-8"), "" + i);
		}
		
		assertThat(trie.find("\u0000".getBytes("UTF-8")), equalTo("0"));
	}
	
	@Test
	public void testDoubleSubsumedNodes() throws Exception {
		trie.insert(new StringBuilder("and wood to fire").reverse().toString().getBytes("UTF-8"), "and wood to fire");
		trie.insert(new StringBuilder("Then shalt thou enquire").reverse().toString().getBytes("UTF-8"), "Then shalt thou enquire");
		trie.insert(new StringBuilder("fire").reverse().toString().getBytes("UTF-8"), "fire");

		assertThat(trie.find(new StringBuilder("and wood to fire").reverse().toString().getBytes("UTF-8")), equalTo("and wood to fire"));
		assertThat(trie.find(new StringBuilder("Then shalt thou enquire").reverse().toString().getBytes("UTF-8")), equalTo("Then shalt thou enquire"));
		assertThat(trie.find(new StringBuilder("fire").reverse().toString().getBytes("UTF-8")), equalTo("fire"));
	}
		
	@Test
	public void testStrings() throws Exception {
		String a = (char) 0 + "a";
		String b = (char) 0 + "b";
		trie.insert(a.getBytes("UTF-8"), "A");
		trie.insert(b.getBytes("UTF-8"), "B");

		assertThat(trie.find(a.getBytes("UTF-8")), equalTo("A"));
		assertThat(trie.find(b.getBytes("UTF-8")), equalTo("B"));
	}

	@Test
	public void testAsNodeSingleNode() throws Exception {
		byte[] bachelor = "bachelor".getBytes("UTF-8");
		trie.insert(bachelor, "Bachelor");

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
		trie.insert(bachelor, "Bachelor");
		byte[] jar = "jar".getBytes("UTF-8");
		trie.insert(jar, "Jar");

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
		trie.insert(bachelor, "Bachelor");
		byte[] jar = "jar".getBytes("UTF-8");
		trie.insert(jar, "Jar");
		byte[] badge = "badge".getBytes("UTF-8");
		trie.insert(badge, "Badge");

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
		trie.insert(bachelor, "Bachelor");
		byte[] jar = "jar".getBytes("UTF-8");
		trie.insert(jar, "Jar");
		byte[] badge = "badge".getBytes("UTF-8");
		trie.insert(badge, "Badge");
		byte[] baby = "baby".getBytes("UTF-8");
		trie.insert(baby, "Baby");

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
		trie.insert(bachelor, "Bachelor");
		byte[] bac = "bac".getBytes("UTF-8");
		trie.insert(bac, "Bac");

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

	@SuppressWarnings("unchecked")
	@Test
	public void testAsNodeMultipleReattachments() throws Exception {
		byte[] bachelor = "bachelor".getBytes("UTF-8");
		trie.insert(bachelor, "Bachelor");
		byte[] bac = "bac".getBytes("UTF-8");
		trie.insert(bac, "Bac");

		((AttachmentAdaptor<String>) trie.navigator()
		.nextNode(bachelor[0])
		.nextNode(bachelor[1])
		.nextNode(bachelor[2])
		.nextNode(bachelor[3])
		.nextNode(bachelor[4]))
		.attach("Bache");

		((AttachmentAdaptor<String>) trie.navigator()
		.nextNode(bachelor[0])
		.nextNode(bachelor[1]))
		.attach("Ba");

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
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.nextNode(bachelor[2])
			.nextNode(bachelor[3])
			.nextNode(bachelor[4])
			.getAttached(), equalTo("Bache"));
		assertThat(trie.navigator()
			.nextNode(bac[0])
			.nextNode(bac[1])
			.nextNode(bac[2])
			.getAttached(), equalTo("Bac"));
		assertThat(trie.navigator()
			.nextNode(bachelor[0])
			.nextNode(bachelor[1])
			.getAttached(), equalTo("Ba"));
	}

}
