package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static java.lang.Integer.MAX_VALUE;
import static java.util.function.Predicate.not;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import de.atruvia.ase.samman.buli.domain.Paarung.Entry;
import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;

public final class PaarungMother {

	private PaarungMother() {
		super();
	}

	public static List<Paarung> createPaarungen(String firstTeam, Ergebnis... ergebnisse) {
		List<String> opponents = opponents(firstTeam, ergebnisse.length);
		return range(0, ergebnisse.length)
				.mapToObj(i -> swapIfOdd(i, paarung(firstTeam, opponents.get(i), ergebnisse[i]))).toList();
	}

	private static Paarung swapIfOdd(int index, Paarung paarung) {
		if (index % 2 == 0) {
			return paarung;
		}
		return paarung.toBuilder().heim(paarung.gast()).gast(paarung.heim()).build();
	}

	public static Paarung paarung(String heimTeam, String gastTeam, Ergebnis ergebnis) {
		PaarungBuilder builder = PaarungBuilder.paarung(heimTeam, gastTeam);
		return (switch (ergebnis) {
		case SIEG -> builder.endergebnis(MAX_VALUE, 0);
		case UNENTSCHIEDEN -> builder.endergebnis(MAX_VALUE, MAX_VALUE);
		case NIEDERLAGE -> builder.endergebnis(0, MAX_VALUE);
		}).build();
	}

	public static Paarung paarungWithAllAttributesSet() {
		return new Paarung(BEENDET, //
				new Entry("IdentifierHeim", "Heim", URI.create("WappenHeim"), 1), //
				new Entry("IdentifierGast", "Gast", URI.create("WappenGast"), 2) //
		);
	}

	private static List<String> opponents(String firstTeam, int count) {
		return rangeClosed(1, MAX_VALUE).mapToObj(i -> "Opponent-" + i).filter(not(firstTeam::equals)).limit(count)
				.toList();
	}

	public static List<Paarung> onMatchday3TeamHasWonOnMatchdayNo1ThenDrawOnMatchdayNo2ThenLossOnMatchdayNo3() {
		return createPaarungen("anyTeamName", SIEG, UNENTSCHIEDEN, NIEDERLAGE);
	}

	public static void team1IsCurrentlyPlaying() {
		paarungen( //
				PaarungBuilder.paarung("Team 1", "Team 4").zwischenergebnis(MAX_VALUE, 0), //
				PaarungBuilder.paarung("Team 2", "Team 3").endergebnis(0, 0) //
		);
	}

	private static List<Paarung> paarungen(PaarungBuilder... paarungen) {
		return Stream.of(paarungen).map(PaarungBuilder::build).toList();
	}

}
