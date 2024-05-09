package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.AUSWAERTS;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.HEIM;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

class TabellenPlatzTest {

	TabellenPlatz anyPlatz = TabellenPlatz.builder().team("team1").wappen(URI.create("proto://wappen1")).build();

	/**
	 * Dieser Test ist ein Detailtest, welcher nicht nach TDD outside-in entstanden
	 * ist. Er dient dazu, die {@link TabellenPlatz#mergeWith(TabellenPlatz)}
	 * Methode für Lesende zu veranschaulichen.
	 */
	@Test
	void testMergeWith() {
		TabellenPlatz entry1 = anyPlatz.toBuilder().spiele(1).punkte(24) //
				.toreUndGegentore(AUSWAERTS, 3, 4) //
				.toreUndGegentore(HEIM, 5, 6) //
				.build();
		TabellenPlatz entry2 = anyPlatz.toBuilder().spiele(2).punkte(1) //
				.toreUndGegentore(AUSWAERTS, 7, 8) //
				.build();
		TabellenPlatz mergedEntry = entry1.mergeWith(entry2);
		assertThat(mergedEntry).isEqualTo(anyPlatz.toBuilder().spiele(1 + 2).punkte(24 + 1) //
				.toreUndGegentore(HEIM, 5, 6) //
				.toreUndGegentore(AUSWAERTS, 3 + 7, 4 + 8) //
				.build());
	}

}
