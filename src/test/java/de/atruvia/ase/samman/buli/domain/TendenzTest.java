package de.atruvia.ase.samman.buli.domain;

import static java.lang.Math.max;
import static java.util.Collections.nCopies;
import static java.util.Collections.reverse;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz.Tendenz;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

class TendenzTest {

	@Property
	void asciiStringAlwaysAsLongAsLength(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = 34) int length) {
		var value = tendenzAsString(ergebnisse, length);
		assertThat(value.chars()).hasSize(length);
	}

	@Property
	void asciiStringOnlyContainsSUNorDash(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = 34) int length) {
		var value = tendenzAsString(ergebnisse, length);
		var allowedChars = "SUN-";
		assertThat(value.chars()).allSatisfy(c -> {
			assertThat(allowedChars.indexOf(c))
					.withFailMessage(() -> "'%s' in '%s' is not one of '%s'".formatted(c, value, allowedChars))
					.isNotNegative();
		});

	}

	@Property
	void containsTheLastNelements(@ForAll List<Ergebnis> ergebnisse, @ForAll @IntRange(min = 0, max = 34) int length) {
		var value = tendenzAsString(ergebnisse, length);
		var expected = reversedSubListOfSize(ergebnisse, length).stream()
				.map(e -> e == null ? "-" : String.valueOf(e.charValue())).collect(joining());
		assertThat(value).isEqualTo(expected);
	}

	private String tendenzAsString(List<Ergebnis> ergebnisse, int length) {
		return Tendenz.fromLatestGameAtEnd(ergebnisse, length).toASCIIString();
	}

	private static List<Ergebnis> reversedSubListOfSize(List<Ergebnis> ergebnisse, int size) {
		List<Ergebnis> list = new ArrayList<>(ergebnisse.subList(max(0, ergebnisse.size() - size), ergebnisse.size()));
		reverse(list);
		list.addAll(nCopies(max(0, size - list.size()), null));
		return list;
	}

}
