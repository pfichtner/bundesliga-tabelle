package de.atruvia.ase.samman.buli.domain;

import static java.lang.Math.max;
import static java.util.Collections.reverse;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz.Tendenz;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

class TendenzTest {

	@Property
	void asciiStringAlwaysAsLongAsLength(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = 34) int length) {
		var value = Tendenz.from(ergebnisse, length).toASCIIString();
		assertThat(value.chars()).hasSize(length);
	}

	@Property
	void asciiStringOnlyContainsSUNorDash(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = 34) int length) {
		var value = Tendenz.from(ergebnisse, length).toASCIIString();
		assertThat(value.chars()).allMatch(c -> "SUN-".indexOf(c) != -1);
	}

	@Property
	void containsTheLastNelements(@ForAll List<Ergebnis> ergebnisse, @ForAll @IntRange(min = 0, max = 34) int length) {
		String expected = subList(ergebnisse, length).stream().map(e -> e == null ? "-" : String.valueOf(e.charValue()))
				.collect(joining());
		var value = Tendenz.from(ergebnisse, length).toASCIIString();
		assertThat(value).isEqualTo(expected);
	}

	private List<Ergebnis> subList(List<Ergebnis> ergebnisse, int length) {
		List<Ergebnis> list = new ArrayList<>(
				ergebnisse.subList(max(0, ergebnisse.size() - length), ergebnisse.size()));
		reverse(list);
		while (list.size() < length) {
			list.add(null);
		}
		return list;
	}

}
