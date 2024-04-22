package de.atruvia.ase.samman.buli.util;

import static lombok.AccessLevel.PRIVATE;

import java.util.function.BinaryOperator;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Streams {

	public static <T> BinaryOperator<T> toOnlyElement() {
		return (f, s) -> {
			throw new IllegalStateException("Expected at most one element but found at least " + f + " and " + s);
		};
	}

}
