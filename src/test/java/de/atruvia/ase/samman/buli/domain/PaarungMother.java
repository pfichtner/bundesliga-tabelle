package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.function.Predicate.not;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

import java.net.URI;
import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung.Entry;
import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;

public final class PaarungMother {

	private PaarungMother() {
		super();
	}

	public static List<Paarung> paarungen(String firstTeam, Ergebnis... ergebnisse) {
		List<String> opponents = opponents(firstTeam, ergebnisse.length);
		return range(0, ergebnisse.length)
				.mapToObj(i -> swapIfOdd(i, paarung(firstTeam, opponents.get(i), ergebnisse[i]))).toList();
	}

	private static Paarung swapIfOdd(int index, Paarung paarung) {
		return index % 2 == 0 ? paarung : paarung.swap();
	}

	public static Paarung paarung(String heimTeam, String gastTeam, Ergebnis ergebnis) {
		Paarung paarung = new Paarung(null, new Entry(heimTeam, null), new Entry(gastTeam, null));
		return (switch (ergebnis) {
		case SIEG -> paarung.endergebnis(MAX_VALUE, MIN_VALUE);
		case UNENTSCHIEDEN -> paarung.endergebnis(MAX_VALUE, MAX_VALUE);
		case NIEDERLAGE -> paarung.endergebnis(MIN_VALUE, MAX_VALUE);
		});
	}

	public static Paarung paarungWithAllAttributesSet() {
		return new Paarung(BEENDET, new Entry("Heim", URI.create("WappenHeim"), 1),
				new Entry("Gast", URI.create("WappenGast"), 2));
	}

	private static List<String> opponents(String firstTeam, int count) {
		return rangeClosed(1, MAX_VALUE).mapToObj(i -> "Opponent-" + i).filter(not(firstTeam::equals)).limit(count)
				.toList();
	}

}
