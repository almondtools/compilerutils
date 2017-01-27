package net.amygdalum.util.text;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.amygdalum.util.builders.ArrayLists;
import net.amygdalum.util.builders.HashMaps;

public final class ByteEncoding {

	private static final Map<Charset, List<ByteRange>> partitionings = initPartitionings();

	private static Map<Charset, List<ByteRange>> initPartitionings() {
		return HashMaps.<Charset, List<ByteRange>> hashed()
			.put(UTF_8, ArrayLists.<ByteRange> list()
				.add(new ByteRange((byte) 0b0, (byte) 0b0111_1111, 128))
				.add(new ByteRange(new byte[]{(byte) 0b1100_0010, (byte) 0b1000_0000}, new byte[]{(byte) 0b1101_1111, (byte) 0b1011_1111}, 1920))
				.add(new ByteRange(new byte[]{(byte) 0b1110_0000, (byte) 0b1010_0000, (byte) 0b1000_0000}, new byte[]{(byte) 0b1110_1111, (byte) 0b1011_1111, (byte) 0b1011_1111}, 63488))
				.build())
			.build();
	}


	public static List<ByteRange> getPartitioningFor(Charset charset) {
		List<ByteRange> part = partitionings.get(charset);
		if (part == null) {
			part = bruteForce(charset);
			partitionings.put(charset, part);
		}
		return part;
	}

	private static List<ByteRange> bruteForce(Charset charset) {
		List<ByteRange> ranges = new ArrayList<>();
		byte[] start = null;
		int size = 0;
		byte[] last = null;
		for (int i = Character.MIN_VALUE; i <= Character.MAX_VALUE; i++) {
			byte[] current = encode((char) i);
			if (start == null) {
				start = current;
				last = current;
				size = 1;
			} else if (start.length == current.length) {
				last = current;
				size++;
			} else {
				ranges.add(new ByteRange(start, last, size));
				start = current;
				last = current;
				size = 1;
			}
			
		}
		return ranges;
	}

	public static byte[] encode(String pattern) {
		return encode(pattern, UTF_8);
	}

	public static byte[] encode(String pattern, Charset charset) {
		try {
			CharsetEncoder encoder = charset.newEncoder()
				.onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT);
			ByteBuffer buffer = encoder.encode(CharBuffer.wrap(pattern));
			byte[] encoded = new byte[buffer.limit()];
			buffer.get(encoded);
			return encoded;
		} catch (CharacterCodingException e) {
			return new byte[0];
		}
	}

	public static byte[] encode(char pattern) {
		return encode(pattern, UTF_8);
	}

	public static byte[] encode(char pattern, Charset charset) {
		try {
			CharsetEncoder encoder = charset.newEncoder()
				.onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT);
			ByteBuffer buffer = encoder.encode(CharBuffer.wrap(new char[] { pattern }));
			byte[] encoded = new byte[buffer.limit()];
			buffer.get(encoded);
			return encoded;
		} catch (CharacterCodingException e) {
			return new byte[0];
		}
	}

	public static String decode(byte[] pattern) {
		return decode(pattern, UTF_8);
	}

	public static String decode(byte[] pattern, Charset charset) {
		try {
			CharsetDecoder decoder = charset.newDecoder()
				.onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT);
			CharBuffer buffer = decoder.decode(ByteBuffer.wrap(pattern));
			return buffer.toString();
		} catch (CharacterCodingException e) {
			return "";
		}
	}

	public static List<ByteRange> intervals(Charset charset, char from, char to) {
		byte[] bytesFrom = encode(from, charset);
		byte[] bytesTo = encode(to, charset);

		List<ByteRange> partitioning = getPartitioningFor(charset);

		List<ByteRange> intervals = new ArrayList<>(partitioning.size());
		char base = 0;
		for (ByteRange part : partitioning) {
			if (from >= base && from < base + part.size() && to >= base && to < base + part.size()) {
				intervals.add(new ByteRange(bytesFrom, bytesTo, (int) to - from));
			} else if (from >= base && from < base + part.size()) {
				intervals.add(new ByteRange(bytesFrom, part.to, (int) base + part.size() - from));
			} else if (from < base && to >= base + part.size()) {
				intervals.add(part);
			} else if (to >= base && to < base + part.size()) {
				intervals.add(new ByteRange(part.from, bytesTo, (int) to - base));
			}
			base += part.size();
		}
		return intervals;
	}
}
