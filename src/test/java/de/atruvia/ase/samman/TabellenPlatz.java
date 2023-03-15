package de.atruvia.ase.samman;

import static de.atruvia.ase.samman.TabelleTest.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.TabelleTest.Ergebnis.SIEG;
import static de.atruvia.ase.samman.TabelleTest.Ergebnis.UNENTSCHIEDEN;
import static java.util.Collections.emptyMap;

import java.util.HashMap;
import java.util.Map;

import de.atruvia.ase.samman.TabelleTest.Ergebnis;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class TabellenPlatz {

//	static class TabellenPlatzBuilder {
//
////			private final Map<Ergebnis, Integer> ergebnisse = new HashMap<Ergebnis, Integer>();
//
//		public TabellenPlatz.TabellenPlatzBuilder ergebnis(Ergebnis ergebnis) {
//			ergebnisse.merge(ergebnis, 1, (a, b) -> a + b);
//			return this;
//		}
//
//	}

	static TabellenPlatz NULL = new TabellenPlatz(0, "", 0, emptyMap(), 0, 0, 0);

	int platz;
	String team;
	@Builder.Default
	int spiele = 1;
	@Builder.Default
	Map<Ergebnis, Integer> ergebnisse = new HashMap<>();
	int punkte;
	int tore;
	int gegentore;

	public int getTorDifferenz() {
		return tore - gegentore;
	}

	public TabellenPlatz merge(TabellenPlatz other) {
		return TabellenPlatz.builder() //
				.ergebnisse(merge(this.ergebnisse, other.ergebnisse)) //
				.spiele(this.spiele + other.spiele) //
				.punkte(this.punkte + other.punkte) //
				.tore(this.tore + other.tore) //
				.gegentore(this.gegentore + other.gegentore) //
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