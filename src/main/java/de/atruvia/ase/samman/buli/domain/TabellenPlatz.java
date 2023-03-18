package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
public class TabellenPlatz {

	static TabellenPlatz NULL = new TabellenPlatz(null, 0, "", 0, emptyMap(), emptyList(), 0, 0, 0, 0, 0);

	@With
	URI wappen;
	@With
	int platz;
	@With
	String team;
	@Builder.Default
	int spiele = 1;
	Map<Ergebnis, Integer> ergebnisseOld;
	List<Ergebnis> ergebnisse;
	int punkte;
	int toreHeim;
	int toreAuswaerts;
	int gegentoreHeim;
	int gegentoreAuswaerts;

	public int getTore() {
		return toreHeim + toreAuswaerts;
	}

	public int getGegentore() {
		return gegentoreHeim + gegentoreAuswaerts;
	}

	public static class TabellenPlatzBuilder {

		public TabellenPlatzBuilder() {
			ergebnisse = new ArrayList<>();
			ergebnisseOld = new HashMap<>();
		}

		public TabellenPlatzBuilder ergebnis(Ergebnis ergebnis) {
			ergebnisse.add(ergebnis);
			ergebnisseOld.merge(ergebnis, 1, (a, b) -> a + b);
			return this;
		}

	}

	public int getTorDifferenz() {
		return getTore() - getGegentore();
	}

	public TabellenPlatz merge(TabellenPlatz other) {
		return builder() //
				.ergebnisse(merge(this.ergebnisse, other.ergebnisse))
				.ergebnisseOld(merge(this.ergebnisseOld, other.ergebnisseOld)) //
				.spiele(this.spiele + other.spiele) //
				.punkte(this.punkte + other.punkte) //
				.toreHeim(this.toreHeim + other.toreHeim) //
				.toreAuswaerts(this.toreAuswaerts + other.toreAuswaerts) //
				.gegentoreHeim(this.gegentoreHeim + other.gegentoreHeim) //
				.gegentoreAuswaerts(this.gegentoreAuswaerts + other.gegentoreAuswaerts) //
				.wappen(other.wappen == null ? this.wappen : other.wappen) //
				.build();
	}

	private List<Ergebnis> merge(List<Ergebnis> ergebnisse1, List<Ergebnis> ergebnisse2) {
		return Stream.concat(ergebnisse1.stream(), ergebnisse2.stream()).collect(toList());
	}

	private static Map<Ergebnis, Integer> merge(Map<Ergebnis, Integer> map1, Map<Ergebnis, Integer> map2) {
		return Stream.of(map1, map2).map(Map::entrySet).flatMap(Collection::stream)
				.collect(toMap(Entry::getKey, Entry::getValue, Integer::sum));
	}

	public int getSiege() {
		return ergebnis(SIEG);
	}

	public int getUnentschieden() {
		return ergebnis(UNENTSCHIEDEN);
	}

	public int getNiederlagen() {
		return ergebnis(NIEDERLAGE);
	}

	private int ergebnis(Ergebnis type) {
		return ergebnisseOld.getOrDefault(type, 0);
	}

}