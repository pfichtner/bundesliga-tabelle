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
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
public class TabellenPlatz {

	@Value
	private static class ErgebnisEntry {
		Ergebnis ergebnis;
		ErgebnisTyp ergebnisTyp;
	}

	@Value
	public static class ToreUndGegentore {
		private static final ToreUndGegentore NULL = new ToreUndGegentore(0, 0);

		private ToreUndGegentore merge(ToreUndGegentore other) {
			return new ToreUndGegentore(tore + other.tore, gegentore + other.gegentore);
		}

		int tore;
		int gegentore;
	}

	static TabellenPlatz NULL = new TabellenPlatz(null, 0, "", 0, emptyList(), 0, ToreUndGegentore.NULL,
			ToreUndGegentore.NULL);

	URI wappen;
	@With
	int platz;
	@With
	String team;
	@Builder.Default
	int spiele = 1;
	List<ErgebnisEntry> ergebnisse;
	int punkte;
	@Builder.Default
	ToreUndGegentore heim = ToreUndGegentore.NULL;
	@Builder.Default
	ToreUndGegentore auswaerts = ToreUndGegentore.NULL;

	public List<Ergebnis> getErgebnisse() {
		return getErgebnisse(ErgebnisTyp.values());
	}

	public List<Ergebnis> getErgebnisse(ErgebnisTyp... ergebnisTyp) {
		return ergebnisse.stream().filter(e -> asList(ergebnisTyp).contains(e.ergebnisTyp()))
				.map(ErgebnisEntry::ergebnis).toList();
	}

	public int tore() {
		return heim.tore + auswaerts.tore;
	}

	public int gegentore() {
		return heim.gegentore + auswaerts.gegentore;
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

	public int torDifferenz() {
		return tore() - gegentore();
	}

	public TabellenPlatz merge(TabellenPlatz other) {
		return builder() //
				.ergebnisse(merge(this.ergebnisse, other.ergebnisse)) //
				.spiele(merge(this.spiele, other.spiele)) //
				.punkte(merge(this.punkte, other.punkte)) //
				.heim(this.heim.merge(other.heim)) //
				.auswaerts(this.auswaerts.merge(other.auswaerts)) //
				.wappen(other.wappen == null ? this.wappen : other.wappen) //
				.build();
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