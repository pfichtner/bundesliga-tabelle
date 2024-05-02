package de.atruvia.ase.samman.buli.util;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Merger {

	public static interface Mergeable<T> {
		T merge(T other);
	}

	public static <T extends Mergeable<T>> T merge(T first, T second) {
		return first.merge(second);
	}

	public static int merge(int... values) {
		return IntStream.of(values).sum();
	}

	@SafeVarargs
	public static <T> List<T> merge(List<T>... lists) {
		return stream(lists).flatMap(List::stream).toList();
	}

	@SafeVarargs
	public static <K, V extends Mergeable<V>> Map<K, V> merge(Map<K, V>... maps) {
		return Stream.of(maps).map(Map<K, V>::entrySet).flatMap(Set<Entry<K, V>>::stream)
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v1.merge(v2), HashMap::new));
	}

	@SafeVarargs
	public static <T> T lastNonNull(T... objects) {
		// could be done with streams as well but then we would consume much more
		// elements then we have to
		// stream(objects).filter(Objects::nonNull).reduce(lastElement()).orElse(null);
		for (var it = asList(objects).listIterator(objects.length); it.hasPrevious();) {
			T prev;
			if ((prev = it.previous()) != null) {
				return prev;
			}
		}
		return null;
	}

}
