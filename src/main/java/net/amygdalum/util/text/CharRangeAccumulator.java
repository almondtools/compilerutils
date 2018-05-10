package net.amygdalum.util.text;

import static java.lang.Character.MAX_VALUE;
import static java.lang.Character.MIN_VALUE;

import java.util.List;

import net.amygdalum.util.builders.ArrayLists;

public class CharRangeAccumulator {

	private List<CharRange> ranges;

	public CharRangeAccumulator() {
		ranges = ArrayLists.of(new CharRange(MIN_VALUE, MAX_VALUE));
	}

	public List<CharRange> getRanges() {
		return ranges;
	}

	public void split(char from, char to) {
		for (int i = 0; i < ranges.size(); i++) {
			CharRange currentRange = ranges.get(i);
			if (currentRange.contains(from) && currentRange.contains(to)) {
				i = replace(i, currentRange.splitAround(from, to));
			} else if (currentRange.contains(from)) {
				i = replace(i, currentRange.splitBefore(from));
			} else if (currentRange.contains(to)) {
				i = replace(i, currentRange.splitAfter(to));
			}
		}
	}

	public int replace(int i, List<CharRange> replacement) {
		ranges.remove(i);
		ranges.addAll(i, replacement);
		return i + replacement.size() - 1;
	}

}