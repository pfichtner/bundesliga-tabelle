package de.atruvia.ase.samman.buli.domain;

import static java.lang.Math.max;
import static java.util.Collections.reverse;
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
	private static final int MAX_LENGTH = 99;

	@Property
	void tendenzHatNieMehrErgebnisseAlsErgebnisse(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = MAX_LENGTH) int length) {
		var sut = Tendenz.fromLatestGameAtEnd(ergebnisse, length);
		assertThat(sut.ergebnisse()).hasSizeLessThanOrEqualTo(ergebnisse.size());
	}

	@Property
	void tendenzHatNieMehrErgebnisseAlsLength(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = MAX_LENGTH) int length) {
		var sut = Tendenz.fromLatestGameAtEnd(ergebnisse, length);
		assertThat(sut.ergebnisse()).hasSizeLessThanOrEqualTo(length);
	}

	@Property
	void containsTheLastNelements(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = MAX_LENGTH) int length) {
		var sut = Tendenz.fromLatestGameAtEnd(ergebnisse, length);
		var content = reversedSubListOfSize(ergebnisse, length).stream().toList();
		assertThat(sut.ergebnisse()).containsExactlyElementsOf(content);
	}

	private static List<Ergebnis> reversedSubListOfSize(List<Ergebnis> list, int size) {
		List<Ergebnis> reversedSublist = new ArrayList<>(list.subList(max(0, list.size() - size), list.size()));
		reverse(reversedSublist);
		return reversedSublist;
	}

}
