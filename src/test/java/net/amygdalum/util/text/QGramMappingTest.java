package net.amygdalum.util.text;

import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.intArrayContaining;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.arrayContaining;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class QGramMappingTest {

    @Test
    public void testMap28() throws Exception {
        QGramMapping mapper = new QGramMapping(2, 8);

        assertThat(mapper.map(chars(1, 2), 0), equalTo((1 << 8) + 2));
        assertThat(mapper.map(chars(1, 2, 4, 3), 2), equalTo((4 << 8) + 3));
        assertThat(mapper.map(chars(255, 254), 0), equalTo((255 << 8) + 254));
    }

    @Test
    public void testMap38() throws Exception {
        QGramMapping mapper = new QGramMapping(3, 8);

        assertThat(mapper.map(chars(1, 2, 3), 0), equalTo((1 << 16) + (2 << 8) + 3));
        assertThat(mapper.map(chars(1, 4, 3, 2), 1), equalTo((4 << 16) + (3 << 8) + 2));
        assertThat(mapper.map(chars(255, 254, 253), 0), equalTo((255 << 16) + (254 << 8) + 253));
    }

    @Test
    public void testMap24() throws Exception {
        QGramMapping mapper = new QGramMapping(2, 4);

        assertThat(mapper.map(chars(1, 2), 0), equalTo((1 << 4) + 2));
        assertThat(mapper.map(chars(1, 2, 4, 3), 2), equalTo((4 << 4) + 3));
        assertThat(mapper.map(chars(255, 254), 0), equalTo((15 << 4) + 14));
    }

    @Test
    public void testMap34() throws Exception {
        QGramMapping mapper = new QGramMapping(3, 4);

        assertThat(mapper.map(chars(1, 2, 3), 0), equalTo((1 << 8) + (2 << 4) + 3));
        assertThat(mapper.map(chars(1, 4, 3, 2), 1), equalTo((4 << 8) + (3 << 4) + 2));
        assertThat(mapper.map(chars(255, 254, 253), 0), equalTo((15 << 8) + (14 << 4) + 13));
    }

    @Test
    public void testMapCharMapping28() throws Exception {
        QGramMapping mapper = new QGramMapping(2, 8);

        assertThat(mapper.map(chars(1, 2), 0, doubleMapping()), intArrayContaining(
            (1 << 8) + 2,
            (1 << 8) + (2 << 1),
            (1 << 9) + 2,
            (1 << 9) + (2 << 1)));
        assertThat(mapper.map(chars(255, 254), 0, doubleMapping()), intArrayContaining(
            (255 << 8) + 254,
            (255 << 8) + 252,
            (254 << 8) + 254,
            (254 << 8) + 252));
    }

    @Test
    public void testMapCharMapping24() throws Exception {
        QGramMapping mapper = new QGramMapping(2, 4);

        assertThat(mapper.map(chars(1, 2), 0, doubleMapping()), intArrayContaining(
            (1 << 4) + 2,
            (1 << 4) + (2 << 1),
            (1 << 5) + 2,
            (1 << 5) + (2 << 1)));
        assertThat(mapper.map(chars(255, 254), 0, doubleMapping()), intArrayContaining(
            (15 << 4) + 14,
            (15 << 4) + 12,
            (14 << 4) + 14,
            (14 << 4) + 12));
    }

    @Test
    public void testNewQGram() throws Exception {
        assertThat(new QGramMapping(2, 6).newQGram().length, equalTo(2));
        assertThat(new QGramMapping(3, 5).newQGram().length, equalTo(3));
    }

    @Test
    public void testGetQ() throws Exception {
        assertThat(new QGramMapping(2, 6).getQ(), equalTo(2));
        assertThat(new QGramMapping(3, 5).getQ(), equalTo(3));
    }

    private CharMapping doubleMapping() {
        return new CharMapping() {

            @Override
            public char[] normalized(char[] chars) {
                return chars;
            }

            @Override
            public char[] map(char c) {
                return new char[] { c, (char) (c << 1) };
            }
        };
    }

    private char[] chars(int... ints) {
        char[] chars = new char[ints.length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) ints[i];
        }
        return chars;
    }

    @Test
    public void testMapCharArrayWithLongerArrays() throws Exception {
        assertThat(new QGramMapping(2, 4).map("abc".toCharArray(), 1),
            equalTo((('b' & 0b1111) << 4) | ('c' & 0b1111)));
        assertThat(new QGramMapping(2, 4).map("abcd".toCharArray(), 1),
            equalTo((('b' & 0b1111) << 4) | ('c' & 0b1111)));
    }

    @Test(expected = MappingException.class)
    public void testMapCharArrayWithWrongQ() throws Exception {
        new QGramMapping(2, 4).map(new char[3]);
    }

    @Test
    public void testMapCharArray() throws Exception {
        assertThat(new QGramMapping(2, 4).map("ab".toCharArray()),
            equalTo((('a' & 0b1111) << 4) | ('b' & 0b1111)));
        assertThat(new QGramMapping(3, 4).map("abc".toCharArray()),
            equalTo((('a' & 0b1111) << 8) | (('b' & 0b1111) << 4) | ('c' & 0b1111)));
        assertThat(new QGramMapping(2, 6).map("ab".toCharArray()),
            equalTo((('a' & 0b111111) << 6) | ('b' & 0b111111)));
    }

    @Test(expected = MappingException.class)
    public void testMapCharArrayIntWithWrongQ() throws Exception {
        new QGramMapping(2, 4).map(new char[4], 3);
    }

    @Test
    public void testMapCharArrayInt() throws Exception {
        assertThat(new QGramMapping(2, 4).map("xxab".toCharArray(), 2),
            equalTo((('a' & 0b1111) << 4) | ('b' & 0b1111)));
        assertThat(new QGramMapping(3, 4).map("xabc".toCharArray(), 1),
            equalTo((('a' & 0b1111) << 8) | (('b' & 0b1111) << 4) | ('c' & 0b1111)));
        assertThat(new QGramMapping(2, 6).map("ab".toCharArray(), 0),
            equalTo((('a' & 0b111111) << 6) | ('b' & 0b111111)));
    }

    @Test(expected = MappingException.class)
    public void testMapCharArrayCharMappingWithWrongQ() throws Exception {
        new QGramMapping(2, 4).map(new char[3], CharMapping.IDENTITY);
    }

    @Test
    public void testMapCharArrayCharMapping() throws Exception {
        assertThat(new QGramMapping(2, 7).map("ab".toCharArray(), CharMapping.IDENTITY),
            intArrayContaining((('a' & 0b1111111) << 7) | ('b' & 0b1111111)));
        assertThat(new QGramMapping(2, 7).map("ab".toCharArray(), eachCase()),
            intArrayContaining(
                (('a' & 0b1111111) << 7) | ('b' & 0b1111111),
                (('A' & 0b1111111) << 7) | ('b' & 0b1111111),
                (('a' & 0b1111111) << 7) | ('B' & 0b1111111),
                (('A' & 0b1111111) << 7) | ('B' & 0b1111111)).inAnyOrder());
    }

    @Test(expected = MappingException.class)
    public void testMapCharArrayIntCharMappingWithWrongQ() throws Exception {
        new QGramMapping(2, 4).map(new char[3], 2, CharMapping.IDENTITY);
    }

    @Test
    public void testMapCharArrayIntCharMapping() throws Exception {
        assertThat(new QGramMapping(2, 7).map("xabx".toCharArray(), 1, CharMapping.IDENTITY),
            intArrayContaining((('a' & 0b1111111) << 7) | ('b' & 0b1111111)));
        assertThat(new QGramMapping(2, 7).map("xxab".toCharArray(), 2, eachCase()),
            intArrayContaining(
                (('a' & 0b1111111) << 7) | ('b' & 0b1111111),
                (('A' & 0b1111111) << 7) | ('b' & 0b1111111),
                (('a' & 0b1111111) << 7) | ('B' & 0b1111111),
                (('A' & 0b1111111) << 7) | ('B' & 0b1111111)).inAnyOrder());
    }

    @Test(expected=MappingException.class)
    public void testIterateCharArrayWrongQ() throws Exception {
        new QGramMapping(2, 4).iterate("a".toCharArray());
    }

    @Test
    public void testIterateCharArray() throws Exception {
        assertThat(new QGramMapping(2, 4).iterate("abcd".toCharArray()),
            intArrayContaining(
                (('a' & 0b1111) << 4) | ('b' & 0b1111),
                (('b' & 0b1111) << 4) | ('c' & 0b1111),
                (('c' & 0b1111) << 4) | ('d' & 0b1111)));
        assertThat(new QGramMapping(3, 7).iterate("abcdef".toCharArray()),
            intArrayContaining(
                (('a' & 0b1111111) << 14) | (('b' & 0b1111111) << 7) | ('c' & 0b1111111),
                (('b' & 0b1111111) << 14) | (('c' & 0b1111111) << 7) | ('d' & 0b1111111),
                (('c' & 0b1111111) << 14) | (('d' & 0b1111111) << 7) | ('e' & 0b1111111),
                (('d' & 0b1111111) << 14) | (('e' & 0b1111111) << 7) | ('f' & 0b1111111)));
    }

    @Test(expected=MappingException.class)
    public void testIterateCharArrayCharMappingWrongQ() throws Exception {
        new QGramMapping(3, 4).iterate("ab".toCharArray(), CharMapping.IDENTITY);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIterateCharArrayCharMapping() throws Exception {
        assertThat(new QGramMapping(2, 4).iterate("abcd".toCharArray(), CharMapping.IDENTITY), arrayContaining(
            intArrayContaining((('a' & 0b1111) << 4) | ('b' & 0b1111)),
            intArrayContaining((('b' & 0b1111) << 4) | ('c' & 0b1111)),
            intArrayContaining((('c' & 0b1111) << 4) | ('d' & 0b1111))));
        assertThat(new QGramMapping(3, 7).iterate("abc".toCharArray(), eachCase()), arrayContaining(
            intArrayContaining(
                (('a' & 0b1111111) << 14) | (('b' & 0b1111111) << 7) | ('c' & 0b1111111),
                (('A' & 0b1111111) << 14) | (('b' & 0b1111111) << 7) | ('c' & 0b1111111),
                (('a' & 0b1111111) << 14) | (('B' & 0b1111111) << 7) | ('c' & 0b1111111),
                (('a' & 0b1111111) << 14) | (('b' & 0b1111111) << 7) | ('C' & 0b1111111),
                (('A' & 0b1111111) << 14) | (('B' & 0b1111111) << 7) | ('c' & 0b1111111),
                (('a' & 0b1111111) << 14) | (('B' & 0b1111111) << 7) | ('C' & 0b1111111),
                (('A' & 0b1111111) << 14) | (('b' & 0b1111111) << 7) | ('C' & 0b1111111),
                (('A' & 0b1111111) << 14) | (('B' & 0b1111111) << 7) | ('C' & 0b1111111)).inAnyOrder()));
    }

    private CharMapping eachCase() {
        return new CharMapping() {

            @Override
            public char[] normalized(char[] chars) {
                char[] normalized = new char[chars.length];
                for (int i = 0; i < normalized.length; i++) {
                    normalized[i] = Character.toLowerCase(chars[i]);
                }
                return normalized;
            }

            @Override
            public char[] map(char c) {
                return new char[] {
                    Character.toLowerCase(c),
                    Character.toUpperCase(c)
                };
            }
        };
    }

}
