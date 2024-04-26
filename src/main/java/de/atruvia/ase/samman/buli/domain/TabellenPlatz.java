package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class TabellenPlatz {

	@Data
	@AllArgsConstructor
	private static class ErgebnisEntry {
		Ergebnis ergebnis;
		ErgebnisTyp ergebnisTyp;
	}

	@Data
	@AllArgsConstructor
	public static class ToreUndGegentore {
		static final ToreUndGegentore NULL = new ToreUndGegentore(0, 0);

		private ToreUndGegentore merge(ToreUndGegentore other) {
			return new ToreUndGegentore(tore + other.tore, gegentore + other.gegentore);
		}

		int tore;
		int gegentore;
	}

	static TabellenPlatz NULL = new TabellenPlatz(null, 0, "", 0, emptyList(), 0, ToreUndGegentore.NULL,
			ToreUndGegentore.NULL, null);

	URI wappen;
	int platz;
	String team;
	int spiele;
	List<ErgebnisEntry> ergebnisse = new ArrayList<>();
	int punkte;
	ToreUndGegentore heim = ToreUndGegentore.NULL;
	ToreUndGegentore auswaerts = ToreUndGegentore.NULL;
	Paarung laufendesSpiel;

	public List<Ergebnis> getErgebnisse() {
		return getErgebnisse(ErgebnisTyp.values());
	}

	public List<Ergebnis> getErgebnisse(ErgebnisTyp... ergebnisTyp) {
		return ergebnisse.stream().filter(e -> entryErgebnisIsTypeOf(e, ergebnisTyp)).map(ErgebnisEntry::ergebnis)
				.toList();
	}

	public TabellenPlatz ergebnis(Ergebnis ergebnis, ErgebnisTyp ergebnisTyp) {
		ergebnisse.add(new ErgebnisEntry(ergebnis, ergebnisTyp));
		return this;
	}

	private static boolean entryErgebnisIsTypeOf(ErgebnisEntry e, ErgebnisTyp... ergebnisTyp) {
		return asList(ergebnisTyp).contains(e.ergebnisTyp());
	}

	public int tore() {
		return heim.tore + auswaerts.tore;
	}

	public int gegentore() {
		return heim.gegentore + auswaerts.gegentore;
	}

	public int torDifferenz() {
		return tore() - gegentore();
	}

	public TabellenPlatz merge(TabellenPlatz other) {
		return new TabellenPlatz() //
				.ergebnisse(merge(this.ergebnisse, other.ergebnisse)) //
				.spiele(merge(this.spiele, other.spiele)) //
				.punkte(merge(this.punkte, other.punkte)) //
				.heim(this.heim.merge(other.heim)) //
				.auswaerts(this.auswaerts.merge(other.auswaerts)) //
				.wappen(other.wappen == null ? this.wappen : other.wappen) //
				.laufendesSpiel(other.laufendesSpiel == null ? this.laufendesSpiel : other.laufendesSpiel) //
		;
	}

	private static int merge(int value1, int value2) {
		return value1 + value2;
	}

	@SafeVarargs
	private static <T> List<T> merge(List<T>... lists) {
		return Stream.of(lists).flatMap(List::stream).toList();
	}

	public int siege() {
		return countAnzahl(SIEG);
	}

	public int unentschieden() {
		return countAnzahl(UNENTSCHIEDEN);
	}

	public int niederlagen() {
		return countAnzahl(NIEDERLAGE);
	}

	private int countAnzahl(Ergebnis type) {
		return (int) ergebnisse.stream().map(ErgebnisEntry::ergebnis).filter(type::equals).count();
	}

}