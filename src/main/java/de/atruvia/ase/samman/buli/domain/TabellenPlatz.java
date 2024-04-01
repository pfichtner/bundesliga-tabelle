package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Stream.concat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder(toBuilder = true)
public class TabellenPlatz {

	@Value
	private static class ErgebnisEntry {
		Ergebnis ergebnis;
		ErgebnisTyp ergebnisTyp;
	}

	static TabellenPlatz NULL = new TabellenPlatz(null, 0, "", 0, emptyList(), 0, 0, 0, 0, 0);

	@With
	URI wappen;
	@With
	int platz;
	@With
	String team;
	@Builder.Default
	int spiele = 1;
	List<ErgebnisEntry> ergebnisse;
	int punkte;
	int toreHeim;
	int toreAuswaerts;
	int gegentoreHeim;
	int gegentoreAuswaerts;

	public List<Ergebnis> getErgebnisse() {
		return getErgebnisse(ErgebnisTyp.values());
	}

	public List<Ergebnis> getErgebnisse(ErgebnisTyp... ergebnisTyp) {
		return ergebnisse.stream().filter(e -> asList(ergebnisTyp).contains(e.getErgebnisTyp()))
				.map(ErgebnisEntry::getErgebnis).toList();
	}

	public int getTore() {
		return toreHeim + toreAuswaerts;
	}

	public int getGegentore() {
		return gegentoreHeim + gegentoreAuswaerts;
	}

	public static class TabellenPlatzBuilder {

		public TabellenPlatzBuilder() {
			ergebnisse = new ArrayList<>();
		}

		public TabellenPlatzBuilder ergebnis(Ergebnis ergebnis, ErgebnisTyp ergebnisTyp) {
			ergebnisse.add(new ErgebnisEntry(ergebnis, ergebnisTyp));
			return this;
		}

	}

	public int getTorDifferenz() {
		return getTore() - getGegentore();
	}

	public TabellenPlatz merge(TabellenPlatz other) {
		return builder() //
				.ergebnisse(merge(this.ergebnisse, other.ergebnisse)) //
				.spiele(this.spiele + other.spiele) //
				.punkte(this.punkte + other.punkte) //
				.toreHeim(this.toreHeim + other.toreHeim) //
				.toreAuswaerts(this.toreAuswaerts + other.toreAuswaerts) //
				.gegentoreHeim(this.gegentoreHeim + other.gegentoreHeim) //
				.gegentoreAuswaerts(this.gegentoreAuswaerts + other.gegentoreAuswaerts) //
				.wappen(other.wappen == null ? this.wappen : other.wappen) //
				.build();
	}

	private static <T> List<T> merge(List<T> ergebnisse1, List<T> ergebnisse2) {
		return concat(ergebnisse1.stream(), ergebnisse2.stream()).toList();
	}

	public int getAnzahlSiege() {
		return countAnzahl(SIEG);
	}

	public int getAnzahlUnentschieden() {
		return countAnzahl(UNENTSCHIEDEN);
	}

	public int getAnzahlNiederlagen() {
		return countAnzahl(NIEDERLAGE);
	}

	private int countAnzahl(Ergebnis type) {
		return (int) ergebnisse.stream().map(ErgebnisEntry::getErgebnis).filter(type::equals).count();
	}

}