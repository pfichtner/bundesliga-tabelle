package de.atruvia.ase.sammanbuli.domain;

import static de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static java.util.Collections.emptyMap;

import java.util.HashMap;
import java.util.Map;

import de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder class TabellenPlatz {

	static TabellenPlatz NULL = new TabellenPlatz(0, "", 0, emptyMap(), 0, 0, 0, 0, 0);

	@With
	int platz;
	@With
	String team;
	@Builder.Default
	int spiele = 1;
	Map<Ergebnis, Integer> ergebnisse;
	int punkte;
	int toreHeim;
	int toreAuswaerts;
	int gegentoreHeim;
	int gegentoreAuswaerts;

	int getTore() {
		return toreHeim + toreAuswaerts;
	}

	int getGegentore() {
		return gegentoreHeim + gegentoreAuswaerts;
	}

	static class TabellenPlatzBuilder {

		TabellenPlatzBuilder() {
			ergebnisse = new HashMap<>();
		}

		public TabellenPlatz.TabellenPlatzBuilder ergebnis(Ergebnis ergebnis) {
			ergebnisse.merge(ergebnis, 1, (a, b) -> a + b);
			return this;
		}

	}

	public int getTorDifferenz() {
		return getTore() - getGegentore();
	}

	public TabellenPlatz merge(TabellenPlatz other) {
		return TabellenPlatz.builder() //
				.ergebnisse(merge(this.ergebnisse, other.ergebnisse)) //
				.spiele(this.spiele + other.spiele) //
				.punkte(this.punkte + other.punkte) //
				.toreHeim(this.toreHeim + other.toreHeim) //
				.toreAuswaerts(this.toreAuswaerts + other.toreAuswaerts) //
				.gegentoreHeim(this.gegentoreHeim + other.gegentoreHeim) //
				.gegentoreAuswaerts(this.gegentoreAuswaerts + other.gegentoreAuswaerts) //
				.build();
	}

	private static Map<Ergebnis, Integer> merge(Map<Ergebnis, Integer> map1, Map<Ergebnis, Integer> map2) {
		Map<Ergebnis, Integer> map = new HashMap<>();
		map.putAll(map1);
		map.putAll(map2);
		return map;
	}

	public int getGewonnen() {
		return ergebnis(SIEG);
	}

	public int getUnentschieden() {
		return ergebnis(UNENTSCHIEDEN);
	}

	public int getVerloren() {
		return ergebnis(NIEDERLAGE);
	}

	private int ergebnis(Ergebnis type) {
		return ergebnisse.getOrDefault(type, 0);
	}

}