package de.atruvia.ase.samman.buli.domain;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.function.Predicate.not;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

import java.net.URI;
import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;

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
		PaarungBuilder builder = PaarungBuilder.paarung(heimTeam, gastTeam);
		return (switch (ergebnis) {
		case SIEG -> builder.ergebnis(MAX_VALUE, MIN_VALUE);
		case UNENTSCHIEDEN -> builder.ergebnis(MAX_VALUE, MAX_VALUE);
		case NIEDERLAGE -> builder.ergebnis(MIN_VALUE, MAX_VALUE);
		}).build();
	}

	public static Paarung all() {
		return new Paarung(true, "Heim", "Gast", URI.create("WappenHeim"), URI.create("WappenGast"), 1, 2);
	}

	private static List<String> opponents(String firstTeam, int count) {
		return rangeClosed(1, MAX_VALUE).mapToObj(i -> "Opponent-" + i).filter(not(firstTeam::equals)).limit(count)
				.toList();
	}

}
