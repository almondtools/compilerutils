package net.amygdalum.util.bits;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static com.almondtools.conmatch.datatypes.PrimitiveArrayMatcher.intArrayContaining;
import static net.amygdalum.util.bits.BitSet.all;
import static net.amygdalum.util.bits.BitSet.bits;
import static net.amygdalum.util.bits.BitSet.empty;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.util.builders.PrimitiveArrays;

public class BitSetTest {

    @Test
    public void testBitSet() throws Exception {
        assertThat(bits(64, 7), satisfiesDefaultEquality()
            .andEqualTo(bits(64, 7))
            .andNotEqualTo(bits(64, 8))
            .andNotEqualTo(bits(63, 7))
            .andNotEqualTo(bits(63, 8))
            .andNotEqualTo(bits(64, 7, 2))
            .andNotEqualTo(bits(64))
            .includingToString());
    }

    @Test
    public void testBitSetAllBits() throws Exception {
        BitSet all = all(64);
        assertThat(all.nextSetBit(0), equalTo(0));
        assertThat(all.nextClearBit(0), equalTo(-1));
        assertThat(all.allSetBits().length, equalTo(64));
        assertThat(all.allClearBits().length, equalTo(0));
    }

    @Test
    public void testBitSetNoBits() throws Exception {
        BitSet all = empty(64);
        assertThat(all.nextSetBit(0), equalTo(-1));
        assertThat(all.nextClearBit(0), equalTo(0));
        assertThat(all.allSetBits().length, equalTo(0));
        assertThat(all.allClearBits().length, equalTo(64));
    }

    @Test
    public void testNextSetBit64() throws Exception {
        BitSet bits = bits(64, 3, 5, 33, 55, 63);
        assertThat(bits.nextSetBit(0), equalTo(3));
        assertThat(bits.nextSetBit(3), equalTo(3));
        assertThat(bits.nextSetBit(4), equalTo(5));
        assertThat(bits.nextSetBit(5), equalTo(5));
        assertThat(bits.nextSetBit(6), equalTo(33));
        assertThat(bits.nextSetBit(33), equalTo(33));
        assertThat(bits.nextSetBit(34), equalTo(55));
        assertThat(bits.nextSetBit(55), equalTo(55));
        assertThat(bits.nextSetBit(56), equalTo(63));
        assertThat(bits.nextSetBit(63), equalTo(63));
        assertThat(bits.nextSetBit(64), equalTo(-1));
    }

    @Test
    public void testAllSetBits64() throws Exception {
        BitSet bits = bits(64, 3, 5, 33, 55, 63);
        assertThat(bits.allSetBits(), intArrayContaining(3, 5, 33, 55, 63));
        assertThat(bits.not().allClearBits(), intArrayContaining(3, 5, 33, 55, 63));
    }

    @Test
    public void testNextSetBit128() throws Exception {
        BitSet bits = bits(128, 3, 5, 33, 55, 78, 127);
        assertThat(bits.nextSetBit(0), equalTo(3));
        assertThat(bits.nextSetBit(3), equalTo(3));
        assertThat(bits.nextSetBit(4), equalTo(5));
        assertThat(bits.nextSetBit(5), equalTo(5));
        assertThat(bits.nextSetBit(6), equalTo(33));
        assertThat(bits.nextSetBit(33), equalTo(33));
        assertThat(bits.nextSetBit(34), equalTo(55));
        assertThat(bits.nextSetBit(55), equalTo(55));
        assertThat(bits.nextSetBit(56), equalTo(78));
        assertThat(bits.nextSetBit(78), equalTo(78));
        assertThat(bits.nextSetBit(79), equalTo(127));
        assertThat(bits.nextSetBit(127), equalTo(127));
        assertThat(bits.nextSetBit(128), equalTo(-1));
    }

    @Test
    public void testAllSetBit128() throws Exception {
        BitSet bits = bits(128, 3, 5, 33, 55, 78, 127);
        assertThat(bits.allSetBits(), intArrayContaining(3, 5, 33, 55, 78, 127));
        assertThat(bits.not().allClearBits(), intArrayContaining(3, 5, 33, 55, 78, 127));
    }

    @Test
    public void testNextClearBit64() throws Exception {
        BitSet bits = bits(64, 3, 5, 33, 55, 56, 63);
        assertThat(bits.nextClearBit(55), equalTo(57));
        assertThat(bits.nextClearBit(0), equalTo(0));
        assertThat(bits.nextClearBit(3), equalTo(4));
        assertThat(bits.nextClearBit(4), equalTo(4));
        assertThat(bits.nextClearBit(5), equalTo(6));
        assertThat(bits.nextClearBit(6), equalTo(6));
        assertThat(bits.nextClearBit(33), equalTo(34));
        assertThat(bits.nextClearBit(34), equalTo(34));
        assertThat(bits.nextClearBit(55), equalTo(57));
        assertThat(bits.nextClearBit(56), equalTo(57));
        assertThat(bits.nextClearBit(63), equalTo(-1));
        assertThat(bits.nextClearBit(65), equalTo(-1));
    }

    @Test
    public void testNextClearBit128() throws Exception {
        BitSet bits = bits(128, 3, 5, 33, 55, 63, 64, 78, 127);
        assertThat(bits.nextClearBit(0), equalTo(0));
        assertThat(bits.nextClearBit(3), equalTo(4));
        assertThat(bits.nextClearBit(4), equalTo(4));
        assertThat(bits.nextClearBit(5), equalTo(6));
        assertThat(bits.nextClearBit(6), equalTo(6));
        assertThat(bits.nextClearBit(33), equalTo(34));
        assertThat(bits.nextClearBit(34), equalTo(34));
        assertThat(bits.nextClearBit(55), equalTo(56));
        assertThat(bits.nextClearBit(56), equalTo(56));
        assertThat(bits.nextClearBit(63), equalTo(65));
        assertThat(bits.nextClearBit(78), equalTo(79));
        assertThat(bits.nextClearBit(79), equalTo(79));
        assertThat(bits.nextClearBit(127), equalTo(-1));
    }

    @Test
    public void testSize() throws Exception {
        assertThat(bits(2, 1).size(), equalTo(2));
        assertThat(empty(3).size(), equalTo(3));
        assertThat(all(4).size(), equalTo(4));
        assertThat(BitSet.ofLong(4).size(), equalTo(64));
        assertThat(BitSet.ofLong(65, 4).size(), equalTo(128));
    }

    @Test
    public void testGetInRange() throws Exception {
        BitSet bs = BitSet.ofLong(0x0001_0000_0000_0001l);

        assertThat(bs.get(0), is(true));
        assertThat(bs.get(1), is(false));
        assertThat(bs.get(47), is(false));
        assertThat(bs.get(48), is(true));
        assertThat(bs.get(49), is(false));
    }

    @Test
    public void testGetOutRange() throws Exception {
        BitSet bs = all(11);

        assertThat(bs.get(11), is(false));
        assertThat(bs.get(64), is(false));
        assertThat(bs.get(128), is(false));
    }

    @Test
    public void testSetInRange() throws Exception {
        BitSet bs = empty(4);

        bs.set(2);

        assertThat(bs.get(0), is(false));
        assertThat(bs.get(1), is(false));
        assertThat(bs.get(2), is(true));
        assertThat(bs.get(3), is(false));
    }

    @Test
    public void testClearInRange() throws Exception {
        BitSet bs = all(5);

        bs.clear(3);

        assertThat(bs.get(0), is(true));
        assertThat(bs.get(1), is(true));
        assertThat(bs.get(2), is(true));
        assertThat(bs.get(3), is(false));
        assertThat(bs.get(4), is(true));
    }

    @Test
    public void testSetOutRange() throws Exception {
        BitSet bs = empty(4);

        bs.set(4);
        bs.set(64);
        bs.set(128);

        assertThat(bs.get(4), is(false));
        assertThat(bs.get(64), is(false));
        assertThat(bs.get(128), is(false));
        assertThat(bs.size(), equalTo(4));
    }

    @Test
    public void testClearOutRange() throws Exception {
        BitSet bs = all(5);

        bs.clear(5);
        bs.clear(64);
        bs.clear(128);

        assertThat(bs.get(5), is(false));
        assertThat(bs.get(64), is(false));
        assertThat(bs.get(128), is(false));
        assertThat(bs.size(), equalTo(5));
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertThat(ofLong(0l).isEmpty(), is(true));
        assertThat(ofLong(0l, 0l).isEmpty(), is(true));
        assertThat(ofLong(0x0001_0000_0000_0000l).isEmpty(), is(false));
        assertThat(ofLong(0x0001_0000_0000_0000l, 0l).isEmpty(), is(false));
        assertThat(ofLong(0x0000_0000_0010_0000l).isEmpty(), is(false));
        assertThat(ofLong(0l, 0x0000_0000_0010_0000l).isEmpty(), is(false));
        assertThat(empty(5).isEmpty(), is(true));
        assertThat(all(3).isEmpty(), is(false));
    }

    @Test
    public void testNot() throws Exception {
        assertThat(ofLong(0x0001_0000_0000_0010l).not(), equalTo(ofLong(0xfffe_ffff_ffff_ffefl)));
        assertThat(ofLong(0xf001_00c0_a000_0010l).not(), equalTo(ofLong(0x0ffe_ff3f_5fff_ffefl)));
    }

    @Test
    public void testAnd() throws Exception {
        assertThat(ofLong(0x0001_0000_0000_0010l).and(ofLong(0l)), equalTo(ofLong(0l)));
        assertThat(ofLong(0x0001_0000_0000_0010l).and(ofLong(0xffff_ffff_ffff_ffffl)), equalTo(ofLong(0x0001_0000_0000_0010l)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l).and(ofLong(0xf101_b0c0_2001_0b00l)), equalTo(ofLong(0xa001_00c0_2001_0000l)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l, 0x6000_0000_0050_0003l)
            .and(ofLong(0xf101_b0c0_2001_0b00l, 0x2000_0000_0060_0000l)),
            equalTo(ofLong(0xa001_00c0_2001_0000l, 0x2000_0000_0040_0000l)));
        assertThat(ofLong(0x1l)
            .and(ofLong(0x0l, 0x03l)),
            equalTo(ofLong(0x0l, 0x1l)));
        assertThat(ofLong(0x0l, 0x01l)
            .and(ofLong(0x3l)),
            equalTo(ofLong(0x0l, 0x1l)));
    }

    @Test
    public void testOr() throws Exception {
        assertThat(ofLong(0x0001_0000_0000_0010l).or(ofLong(0l)), equalTo(ofLong(0x0001_0000_0000_0010l)));
        assertThat(ofLong(0x0001_0000_0000_0010l).or(ofLong(0xffff_ffff_ffff_ffffl)), equalTo(ofLong(0xffff_ffff_ffff_ffffl)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l).or(ofLong(0xf101_b0c0_2001_0b00l)), equalTo(ofLong(0xf101_b0c0_a00d_0b10l)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l, 0x0030_0b00_0000_0100l)
            .or(ofLong(0xf101_b0c0_2001_0b00l, 0x0050_0600_0000_0040l)),
            equalTo(ofLong(0xf101_b0c0_a00d_0b10l, 0x0070_0f00_0000_0140l)));
        assertThat(ofLong(0x2l)
            .or(ofLong(0x0l, 0x01l)),
            equalTo(ofLong(0x0l, 0x3l)));
        assertThat(ofLong(0x0l, 0x01l)
            .or(ofLong(0x2l)),
            equalTo(ofLong(0x0l, 0x3l)));
    }

    @Test
    public void testAndNot() throws Exception {
       assertThat(ofLong(0x0001_0000_0000_0010l).andNot(ofLong(0l)), equalTo(ofLong(0x0001_0000_0000_0010l)));
        assertThat(ofLong(0x0001_0000_0000_0010l).andNot(ofLong(0xffff_ffff_ffff_ffffl)), equalTo(ofLong(0l)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l).andNot(ofLong(0xf101_b0c0_2001_0b00l)), equalTo(ofLong(0x0000_0000_800c_0010l)));
        assertThat(all(5).andNot(all(4)), equalTo(bits(5, 4)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l, 0x6000_0000_0050_0003l)
            .andNot(ofLong(0xf101_b0c0_2001_0b00l, 0x2000_0000_0060_0000l)),
            equalTo(ofLong(0x0000_0000_800c_0010l, 0x4000_0000_0010_0003l)));
        assertThat(ofLong(0x3l)
            .andNot(ofLong(0x0l, 0x06l).not()),
            equalTo(ofLong(0x0l, 0x2l)));
        assertThat(ofLong(0x1l, 0x03l)
            .andNot(ofLong(0x6l).not()),
            equalTo(ofLong(0x1l, 0x2l)));
    }

    @Test
    public void testOrNot() throws Exception {
        assertThat(ofLong(0x0001_0000_0000_0010l).orNot(ofLong(0l)), equalTo(ofLong(0xffff_ffff_ffff_ffffl)));
        assertThat(ofLong(0x0001_0000_0000_0010l).orNot(ofLong(0xffff_ffff_ffff_ffffl)), equalTo(ofLong(0x0001_0000_0000_0010l)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l).orNot(ofLong(0xf101_b0c0_2001_0b00l)), equalTo(ofLong(0xaeff_4fff_ffff_f4ffl)));
        assertThat(empty(5).orNot(all(4)), equalTo(bits(5, 4)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l, 0x6000_0000_0050_0003l)
            .orNot(ofLong(0xf101_b0c0_2001_0b00l, 0x2000_0000_0060_0000l)),
            equalTo(ofLong(0xaeff_4fff_ffff_f4ffl, 0xffff_ffff_ffdf_ffffl)));
        assertThat(ofLong(0x1l)
            .orNot(ofLong(0x01l, 0x02l).not()),
            equalTo(ofLong(0x1l, 0x3l)));
        assertThat(ofLong(0x0l, 0x01l)
            .orNot(ofLong(0x2l).not()),
            equalTo(ofLong(0xffff_ffff_ffff_ffffl, 0x3l)));
    }

    @Test
    public void testXor() throws Exception {
        assertThat(ofLong(0x0001_0000_0000_0010l).xor(ofLong(0l)), equalTo(ofLong(0x0001_0000_0000_0010l)));
        assertThat(ofLong(0x0001_0000_0000_0010l).xor(ofLong(0xffff_ffff_ffff_ffffl)), equalTo(ofLong(0xfffe_ffff_ffff_ffefl)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l).xor(ofLong(0xf101_b0c0_2001_0b00l)), equalTo(ofLong(0x5100_b000_800c_0b10l)));
        assertThat(empty(5).xor(all(4)), equalTo(bits(5, 0, 1, 2, 3)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l, 0x6000_0000_0050_0003l)
            .xor(ofLong(0xf101_b0c0_2001_0b00l, 0x2000_0000_0060_0000l)),
            equalTo(ofLong(0x5100_b000_800c_0b10l, 0x4000_0000_0030_0003l)));
        assertThat(ofLong(0x1l)
            .xor(ofLong(0x0l, 0x3l)),
            equalTo(ofLong(0x0l, 0x2l)));
        assertThat(ofLong(0x0l, 0x01l)
            .xor(ofLong(0x3l)),
            equalTo(ofLong(0x0l, 0x2l)));
    }

    @Test
    public void testEq() throws Exception {
        assertThat(ofLong(0x0001_0000_0000_0010l).eq(ofLong(0l)), equalTo(ofLong(0xfffe_ffff_ffff_ffefl)));
        assertThat(ofLong(0x0001_0000_0000_0010l).eq(ofLong(0xffff_ffff_ffff_ffffl)), equalTo(ofLong(0x0001_0000_0000_0010l)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l).eq(ofLong(0xf101_b0c0_2001_0b00l)), equalTo(ofLong(0xaeff_4fff_7ff3_f4efl)));
        assertThat(empty(5).eq(all(4)), equalTo(bits(5, 4)));
        assertThat(ofLong(0xa001_00c0_a00d_0010l, 0x6000_0000_0050_0003l)
            .eq(ofLong(0xf101_b0c0_2001_0b00l, 0x2000_0000_0060_0000l)),
            equalTo(ofLong(0xaeff_4fff_7ff3_f4efl, 0xbfff_ffff_ffcf_fffcl)));
        assertThat(ofLong(0x1l)
            .eq(ofLong(0xffff_ffff_ffff_ffffl, 0xffff_ffff_ffff_fff3l)),
            equalTo(ofLong(0x0l, 0x0dl)));
        assertThat(ofLong(0x0l, 0x01l)
            .eq(ofLong(0xffff_ffff_ffff_fff3l)),
            equalTo(ofLong(0xffff_ffff_ffff_ffffl, 0x0dl)));
    }

    @Test
    public void testBitCount() throws Exception {
        assertThat(ofLong(0l).bitCount(), equalTo(0));
        assertThat(ofLong(0x0001_0000_0000_0010l).bitCount(), equalTo(2));
        assertThat(ofLong(0xffff_ffff_ffff_ffffl).bitCount(), equalTo(64));
        assertThat(ofLong(0xa001_00c0_a00d_0010l).bitCount(), equalTo(11));
        assertThat(ofLong(0xf101_b0c0_2001_0b00l).bitCount(), equalTo(16));
        assertThat(empty(5).bitCount(), equalTo(0));
        assertThat(all(5).bitCount(), equalTo(5));
        assertThat(ofLong(0xa001_00c0_a00d_0010l, 0x6000_0000_0050_0003l).bitCount(), equalTo(17));
        assertThat(ofLong(0xf101_b0c0_2001_0b00l, 0x2000_0000_0060_0000l).bitCount(), equalTo(19));
    }

    @Test
    public void testClone() throws Exception {
        BitSet bitset = ofLong(0l);
        BitSet clone = bitset.clone();
        assertThat(clone, not(sameInstance(bitset)));
        assertThat(clone, equalTo(bitset));
    }
    
    private BitSet ofLong(long... bits) {
        PrimitiveArrays.revert(bits);
        return BitSet.ofLong(bits);
    }
}
