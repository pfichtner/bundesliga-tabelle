package de.atruvia.ase.samman.buli.util;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Merger {

	public static interface Mergeable<T> {
		T merge(T other);
	}

	public static <T extends Mergeable<T>> T mergeMergeables(T first, T second) {
		return first.merge(second);
	}

	public static int mergeInts(int... values) {
		return IntStream.of(values).sum();
	}

	@SafeVarargs
	public static <T> List<T> mergeLists(List<T>... lists) {
		return Stream.of(lists).flatMap(List::stream).toList();
	}

	public static <T> T lastIfNotNull(T first, T last) {
		return last == null ? first : last;
	}

}
