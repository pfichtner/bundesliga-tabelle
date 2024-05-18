package de.atruvia.ase.samman.buli.domain;

import static java.lang.Math.max;
import static java.util.Collections.reverse;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.generate;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz.Tendenz;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

class TendenzTest {

	// Mögliche maximale Anzahl an Ergebnissen sind (teams-1)*2, Werte darüber sind
	// daher eigentlich sinnlos
	private static final int MAX_LENGTH = 42;

	@Property
	void asciiStringAlwaysAsLongAsLength(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = MAX_LENGTH) int length) {
		var value = tendenzAsString(ergebnisse, length);
		assertThat(value.chars()).hasSize(length);
	}

	@Property
	void asciiStringOnlyContainsSUNorDash(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = MAX_LENGTH) int length) {
		var value = tendenzAsString(ergebnisse, length);
		var allowedChars = "SUN-";
		assertThat(value.chars()).allSatisfy(c -> {
			assertThat(allowedChars.indexOf(c))
					.withFailMessage(() -> "'%s' in '%s' is not one of '%s'".formatted(c, value, allowedChars))
					.isNotNegative();
		});

	}

	@Property
	void containsTheLastNelements(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = MAX_LENGTH) int length) {
		var value = tendenzAsString(ergebnisse, length);
		var content = reversedSubListOfSize(ergebnisse, length).stream().map(Ergebnis::charValue);
		var filler = generate(() -> '-').limit(max(0, length - ergebnisse.size()));
		var expected = concat(content, filler).map(String::valueOf).collect(joining());
		assertThat(value).isEqualTo(expected);
	}

	private static String tendenzAsString(List<Ergebnis> ergebnisse, int length) {
		return Tendenz.fromLatestGameAtEnd(ergebnisse, length).toASCIIString();
	}

	private static List<Ergebnis> reversedSubListOfSize(List<Ergebnis> list, int size) {
		List<Ergebnis> reversedSublist = new ArrayList<>(list.subList(max(0, list.size() - size), list.size()));
		reverse(reversedSublist);
		return reversedSublist;
	}

}
