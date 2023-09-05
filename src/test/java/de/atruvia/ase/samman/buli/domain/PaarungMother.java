package de.atruvia.ase.samman.buli.domain;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;

import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;

public final class PaarungMother {

	private PaarungMother() {
		super();
	}

	public static List<Paarung> paarungen(String firstTeam, Ergebnis... ergebnisse) {
		List<String> opponents = opponents(firstTeam, ergebnisse.length);
		return range(0, ergebnisse.length).mapToObj(i -> paarung(firstTeam, opponents.get(i), ergebnisse[i])).toList();
	}

	public static Paarung paarung(String heimTeam, String gastTeam, Ergebnis ergebnis) {
		return switch (ergebnis) {
		case SIEG -> sieg(heimTeam, gastTeam);
		case UNENTSCHIEDEN -> unentschieden(heimTeam, gastTeam);
		case NIEDERLAGE -> niederlage(heimTeam, gastTeam);
		};
	}

	private static List<String> opponents(String team0, int count) {
		return rangeClosed(1, count + 1).mapToObj(i -> team0 + "-XXX-" + i).toList();
	}

	private static Paarung niederlage(String heimTeam, String gastTeam) {
		return PaarungBuilder.paarung(heimTeam, gastTeam).ergebnis(MIN_VALUE, MAX_VALUE).build();
	}

	private static Paarung unentschieden(String heimTeam, String gastTeam) {
		return PaarungBuilder.paarung(heimTeam, gastTeam).ergebnis(MAX_VALUE, MAX_VALUE).build();
	}

	private static Paarung sieg(String heimTeam, String gastTeam) {
		return PaarungBuilder.paarung(heimTeam, gastTeam).ergebnis(MAX_VALUE, MIN_VALUE).build();
	}

}
