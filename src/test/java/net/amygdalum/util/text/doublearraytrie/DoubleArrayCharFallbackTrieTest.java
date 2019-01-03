package net.amygdalum.util.text.doublearraytrie;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.util.text.CharAutomaton;
import net.amygdalum.util.text.CharFallbackLinks;
import net.amygdalum.util.text.CharFallbackNavigator;
import net.amygdalum.util.text.CharTrie;
import net.amygdalum.util.text.CharWordSetBuilder;

public class DoubleArrayCharFallbackTrieTest {

	private CharWordSetBuilder<String, CharTrie<String>> builder;

	@Before
	public void before() throws Exception {
		builder = new CharWordSetBuilder<>(new DoubleArrayCharFallbackTrieCompiler<String>());
	}

	@Test
	public void testSingleNode() throws Exception {
		CharTrie<String> trie = builder
			.extend("bachelor".toCharArray(), "Bachelor")
			.build();

		assertThat(trie.contains("bachelor".toCharArray()), is(true));
		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.contains("jar".toCharArray()), is(false));
		assertThat(trie.contains("badge".toCharArray()), is(false));
		assertThat(trie.contains("baby".toCharArray()), is(false));
	}

	@Test
	public void testMultipleNonCollidingNodes() throws Exception {
		CharTrie<String> trie = builder
			.extend("bachelor".toCharArray(), "Bachelor")
			.extend("jar".toCharArray(), "Jar")
			.build();

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.contains("jar".toCharArray()), is(true));
		assertThat(trie.find("jar".toCharArray()), equalTo("Jar"));
	}

	@Test
	public void testMultipleCollidingNodes() throws Exception {
		CharTrie<String> trie = builder
			.extend("bachelor".toCharArray(), "Bachelor")
			.extend("jar".toCharArray(), "Jar")
			.extend("badge".toCharArray(), "Badge")
			.build();

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.find("jar".toCharArray()), equalTo("Jar"));
		assertThat(trie.contains("badge".toCharArray()), is(true));
		assertThat(trie.find("badge".toCharArray()), equalTo("Badge"));
	}

	@Test
	public void testMultipleMoreCollidingNodes() throws Exception {
		CharTrie<String> trie = builder
			.extend("bachelor".toCharArray(), "Bachelor")
			.extend("jar".toCharArray(), "Jar")
			.extend("badge".toCharArray(), "Badge")
			.extend("baby".toCharArray(), "Baby")
			.build();

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.find("jar".toCharArray()), equalTo("Jar"));
		assertThat(trie.find("badge".toCharArray()), equalTo("Badge"));
		assertThat(trie.contains("baby".toCharArray()), is(true));
		assertThat(trie.find("baby".toCharArray()), equalTo("Baby"));
	}

	@Test
	public void testMultipleSubsumingNodes() throws Exception {
		CharTrie<String> trie = builder
			.extend("bac".toCharArray(), "Bac")
			.extend("bachelor".toCharArray(), "Bachelor")
			.build();

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.find("bac".toCharArray()), equalTo("Bac"));
	}

	@Test
	public void testMultipleSubsumedNodes() throws Exception {
		CharTrie<String> trie = builder
			.extend("bachelor".toCharArray(), "Bachelor")
			.extend("bac".toCharArray(), "Bac")
			.build();

		assertThat(trie.find("bachelor".toCharArray()), equalTo("Bachelor"));
		assertThat(trie.find("bac".toCharArray()), equalTo("Bac"));
	}

	@Test
	public void testMultipleSubsumedNodes2() throws Exception {
		CharTrie<String> trie = builder
			.extend("abcd".toCharArray(), "ABCD")
			.extend("ab".toCharArray(), "AB")
			.extend("bc".toCharArray(), "BC")
			.extend("cd".toCharArray(), "CD")
			.build();

		assertThat(trie.find("abcd".toCharArray()), equalTo("ABCD"));
		assertThat(trie.find("ab".toCharArray()), equalTo("AB"));
		assertThat(trie.find("bc".toCharArray()), equalTo("BC"));
		assertThat(trie.find("cd".toCharArray()), equalTo("CD"));
	}

	@Test
	public void testMultipleSubsumedNodes3() throws Exception {
		CharTrie<String> trie = builder
			.extend("aaa".toCharArray(), "AAA")
			.extend("aa".toCharArray(), "AA")
			.extend("a".toCharArray(), "A")
			.build();

		assertThat(trie.find("aaa".toCharArray()), equalTo("AAA"));
		assertThat(trie.find("aa".toCharArray()), equalTo("AA"));
		assertThat(trie.find("a".toCharArray()), equalTo("A"));
	}

	@Test
	public void testAttachments() throws Exception {
		CharTrie<String> trie = builder
			.extend("abc".toCharArray(), "ABC")
			.extend("bcd".toCharArray(), "BCD")
			.build();

		assertThat(trie.find("abc".toCharArray()), equalTo("ABC"));
		assertThat(trie.find("bcd".toCharArray()), equalTo("BCD"));
		assertThat(trie.find("a".toCharArray()), nullValue());
		assertThat(trie.find("b".toCharArray()), nullValue());
		assertThat(trie.find("c".toCharArray()), nullValue());
		assertThat(trie.find("d".toCharArray()), nullValue());
	}

	@Test
	public void testAttachments2() throws Exception {
		CharTrie<String> trie = builder
			.extend("".toCharArray(), "")
			.extend("a".toCharArray(), "A")
			.extend("b".toCharArray(), "B")
			.build();

		assertThat(trie.find("".toCharArray()), equalTo(""));
		assertThat(trie.find("a".toCharArray()), equalTo("A"));
		assertThat(trie.find("b".toCharArray()), equalTo("B"));
		assertThat(trie.find("c".toCharArray()), nullValue());
	}

	@Test
	public void testAttachments3() throws Exception {
		CharTrie<String> trie = builder
			.extend("ab".toCharArray(), "AB")
			.extend("aa".toCharArray(), "AA")
			.build();

		assertThat(trie.find("ab".toCharArray()), equalTo("AB"));
		assertThat(trie.find("aa".toCharArray()), equalTo("AA"));
		assertThat(trie.find("bb".toCharArray()), nullValue());
	}

	@Test
	public void testAttachments4() throws Exception {
		CharTrie<String> trie = builder
			.extend("bb".toCharArray(), "BB")
			.extend("ba".toCharArray(), "BA")
			.extend("bbc".toCharArray(), "BBC")
			.extend("bbd".toCharArray(), "BBD")
			.extend("bbf".toCharArray(), "BBF")
			.extend("bbg".toCharArray(), "BBG")
			.extend("bba".toCharArray(), "BBA")
			.build();

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
		CharTrie<String> trie = builder
			.extend("cc".toCharArray(), "CC")
			.extend("ca".toCharArray(), "CA")
			.extend("ac".toCharArray(), "AC")
			.extend("aa".toCharArray(), "AA")
			.build();

		assertThat(trie.find("cc".toCharArray()), equalTo("CC"));
		assertThat(trie.find("ca".toCharArray()), equalTo("CA"));
		assertThat(trie.find("ac".toCharArray()), equalTo("AC"));
		assertThat(trie.find("aa".toCharArray()), equalTo("AA"));
	}

	@Test
	public void testLargeCharacterSpace() throws Exception {
		CharTrie<String> trie = builder
			.extend("\u9999".toCharArray(), "U9999")
			.extend("\u0000".toCharArray(), "U0000")
			.build();

		assertThat(trie.find("\u9999".toCharArray()), equalTo("U9999"));
		assertThat(trie.find("\u0000".toCharArray()), equalTo("U0000"));
	}

	@Test
	public void testDoubleSubsumedNodes() throws Exception {
		CharTrie<String> trie = builder
			.extend(new StringBuilder("and wood to fire").reverse().toString().toCharArray(), "and wood to fire")
			.extend(new StringBuilder("Then shalt thou enquire").reverse().toString().toCharArray(), "Then shalt thou enquire")
			.extend(new StringBuilder("fire").reverse().toString().toCharArray(), "fire")
			.build();

		assertThat(trie.find(new StringBuilder("and wood to fire").reverse().toString().toCharArray()), equalTo("and wood to fire"));
		assertThat(trie.find(new StringBuilder("Then shalt thou enquire").reverse().toString().toCharArray()), equalTo("Then shalt thou enquire"));
		assertThat(trie.find(new StringBuilder("fire").reverse().toString().toCharArray()), equalTo("fire"));
	}

	@Test
	public void testStrings() throws Exception {
		String a = (char) 0 + "a";
		String b = (char) 0 + "b";
		CharTrie<String> trie = builder
			.extend(a.toCharArray(), "A")
			.extend(b.toCharArray(), "B")
			.build();

		assertThat(trie.find(a.toCharArray()), equalTo("A"));
		assertThat(trie.find(b.toCharArray()), equalTo("B"));
	}

	@Test
	public void testAsNodeSingleNode() throws Exception {
		char[] bachelor = "bachelor".toCharArray();
		CharTrie<String> trie = builder
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
		char[] bachelor = "bachelor".toCharArray();
		char[] jar = "jar".toCharArray();
		CharTrie<String> trie = builder
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
		char[] bachelor = "bachelor".toCharArray();
		char[] jar = "jar".toCharArray();
		char[] badge = "badge".toCharArray();
		CharTrie<String> trie = builder
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
		char[] bachelor = "bachelor".toCharArray();
		char[] jar = "jar".toCharArray();
		char[] badge = "badge".toCharArray();
		char[] baby = "baby".toCharArray();
		CharTrie<String> trie = builder
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
		char[] bachelor = "bachelor".toCharArray();
		char[] bac = "bac".toCharArray();
		CharTrie<String> trie = builder
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
	public void testFallback() throws Exception {
		CharTrie<String> trie = builder
			.extend("gat".toCharArray(), "GAT")
			.extend("cgatggg".toCharArray(), "CGATGGG")
			.work(new CharFallbackLinks())
			.build();

		assertThat(trie.navigator()
			.nextNode('c')
			.nextNode('g')
			.nextNode('a')
			.nextNode('t')
			.nextNode('g')
			.nextNode('g')
			.nextNode('g')
			.getAttached(), equalTo("CGATGGG"));
		assertThat(((CharFallbackNavigator<String, ?>) trie.navigator())
			.nextNode('c')
			.nextNode('g')
			.nextNode('a')
			.nextNode('t')
			.fallback()
			.getAttached(), equalTo("GAT"));
	}

	@Test
	public void testCursor() throws Exception {
		CharTrie<String> trie = builder
			.extend("gat".toCharArray(), "GAT")
			.extend("cgatggg".toCharArray(), "CGATGGG")
			.work(new CharFallbackLinks())
			.build();

		CharAutomaton<String> cursor = trie.cursor();
		assertThat(cursor.accept('c'), is(true));
		assertThat(cursor.accept('g'), is(true));
		assertThat(cursor.accept('a'), is(true));
		assertThat(cursor.accept('t'), is(true));
		assertThat(cursor.hasAttachments(), is(true));
		assertThat(cursor.iterator().next(), equalTo("GAT"));
		assertThat(cursor.accept('g'), is(true));
		assertThat(cursor.accept('g'), is(true));
		assertThat(cursor.accept('g'), is(true));
		assertThat(cursor.hasAttachments(), is(true));
		assertThat(cursor.iterator().next(), equalTo("CGATGGG"));
	}

	@Test
	public void testCursor2() throws Exception {
		CharTrie<String> trie = builder
			.extend("gatc".toCharArray(), "GATC")
			.extend("cgatggg".toCharArray(), "CGATGGG")
			.work(new CharFallbackLinks())
			.build();

		CharAutomaton<String> cursor = trie.cursor();
		assertThat(cursor.accept('c'), is(true));
		assertThat(cursor.accept('g'), is(true));
		assertThat(cursor.accept('a'), is(true));
		assertThat(cursor.accept('t'), is(true));
		assertThat(cursor.accept('c'), is(true));
		assertThat(cursor.hasAttachments(), is(true));
		assertThat(cursor.iterator().next(), equalTo("GATC"));
	}

}
