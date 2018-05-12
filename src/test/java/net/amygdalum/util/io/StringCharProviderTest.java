package net.amygdalum.util.io;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


public class StringCharProviderTest {

	@Test
	public void testRestart() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		provider.restart();
		assertThat(provider.current(), equalTo(0l));
	}
	
	@Test
	public void testNextAtBeginning() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 0);
		assertThat(provider.next(), equalTo('a'));
	}

	@Test
	public void testNextInMiddle() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 1);
		assertThat(provider.next(), equalTo('b'));
	}
	
	@Test
	public void testNextConsumes() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 0);
		assertThat(provider.next(), equalTo('a'));
		assertThat(provider.next(), equalTo('b'));
	}

	@Test
	public void testPrevInMiddle() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 1);
		assertThat(provider.prev(), equalTo('a'));
	}
	
	@Test
	public void testPrevAtEnd() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 4);
		assertThat(provider.prev(), equalTo('d'));
	}
	
	@Test
	public void testPrevConsumes() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 4);
		assertThat(provider.prev(), equalTo('d'));
		assertThat(provider.prev(), equalTo('c'));
	}
	
	@Test
	public void testFinishedAtBeginning() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 0);
		assertThat(provider.finished(), is(false));
	}
	
	@Test
	public void testFinishedInMiddle() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 1);
		assertThat(provider.finished(), is(false));
	}
	
	@Test
	public void testFinishedAtEnd() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 4);
		assertThat(provider.finished(), is(true));
	}
	
	@Test
	public void testLookahead() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 0);
		assertThat(provider.lookahead(), equalTo('a'));
	}
	
	@Test
	public void testLookaheadWithN() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 0);
		assertThat(provider.lookahead(0), equalTo('a'));
		assertThat(provider.lookahead(1), equalTo('b'));
		assertThat(provider.lookahead(2), equalTo('c'));
	}
	
	@Test
	public void testLookbehind() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 4);
		assertThat(provider.lookbehind(), equalTo('d'));
	}
	
	@Test
	public void testLookbehindWithN() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 4);
		assertThat(provider.lookbehind(0), equalTo('d'));
		assertThat(provider.lookbehind(1), equalTo('c'));
		assertThat(provider.lookbehind(2), equalTo('b'));
	}

	@Test
	public void testCurrent() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		assertThat(provider.current(), equalTo(2l));
	}
	
	@Test
	public void testMoveAndCurrent() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		provider.move(3);
		assertThat(provider.current(), equalTo(3l));
	}
	
	@Test
	public void testAtDoesNotConsume() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 0);
		assertThat(provider.at(2), equalTo('c'));
		assertThat(provider.current(), equalTo(0l));
	}
	
	@Test
	public void testSliceDoesNotConsume() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 0);
		assertThat(provider.slice(1, 3), equalTo("bc"));
		assertThat(provider.current(), equalTo(0l));
	}

	@Test
	public void testBetweenDoesNotConsume() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 0);
		assertThat(provider.between(1, 3), equalTo(new char[]{'b','c'}));
		assertThat(provider.current(), equalTo(0l));
	}

	@Test
	public void testToStringAtBeginning() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 0);
		assertThat(provider.toString(), equalTo("|abcd"));
	}
	
	@Test
	public void testToStringInMiddle() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 1);
		assertThat(provider.toString(), equalTo("a|bcd"));
	}
	
	@Test
	public void testToStringAtEnd() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 4);
		assertThat(provider.toString(), equalTo("abcd|"));
	}
	
	@Test
	public void testChangedWithoutChange() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		
		assertThat(provider.changed(), is(false));
	}

	@Test
	public void testChangedWithChange() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		
		provider.mark();
		
		provider.forward(1);
		
		assertThat(provider.changed(), is(true));
	}

	@Test
	public void testChangedWithTemporaryChange() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		
		provider.mark();
		
		provider.next();
		provider.prev();
		
		assertThat(provider.changed(), is(false));
	}

	@Test
	public void testChangedDoubleCall() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		
		provider.mark();
		
		provider.forward(1);
		provider.changed();
		
		assertThat(provider.changed(), is(false));
	}

	@Test
	public void testFinish() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		
		provider.finish();
		
		assertThat(provider.finished(), is(true));
	}

	@Test
	public void testFinished() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		
		assertThat(provider.finished(1), is(false));
		assertThat(provider.finished(2), is(true));
	}

	@Test
	public void testFinishedNotChangesState() throws Exception {
		StringCharProvider provider = new StringCharProvider("abcd", 2);
		
		assertThat(provider.finished(2), is(true));
		assertThat(provider.finished(1), is(false));
	}

}
