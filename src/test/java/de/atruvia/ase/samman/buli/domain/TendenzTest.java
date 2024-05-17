package de.atruvia.ase.samman.buli.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz.Tendenz;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

class TendenzTest {

	@Property
	void asciiStringAlwaysAsLongAsLength(@ForAll List<Ergebnis> ergebnisse,
			@ForAll @IntRange(min = 0, max = 512) int length) {
		String asciiString = Tendenz.from(ergebnisse, length).toASCIIString();
		assertThat(asciiString).hasSize(length);
	}

}
