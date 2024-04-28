package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.util.Merger.lastIfNotNull;
import static de.atruvia.ase.samman.buli.util.Merger.mergeInts;
import static de.atruvia.ase.samman.buli.util.Merger.mergeLists;
import static de.atruvia.ase.samman.buli.util.Merger.mergeMergeables;
import static java.util.Arrays.asList;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp;
import de.atruvia.ase.samman.buli.util.Merger.Mergeable;
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
	public static class ToreUndGegentore implements Mergeable<ToreUndGegentore> {
		private static final ToreUndGegentore NULL = new ToreUndGegentore(0, 0);

		@Override
		public ToreUndGegentore merge(ToreUndGegentore other) {
			return new ToreUndGegentore(tore + other.tore, gegentore + other.gegentore);
		}

		int tore;
		int gegentore;
	}

	URI wappen;
	@With
	int platz;
	@With
	String team;
	int spiele;
	List<ErgebnisEntry> ergebnisse;
	int punkte;
	@Builder.Default
	ToreUndGegentore heim = ToreUndGegentore.NULL;
	@Builder.Default
	ToreUndGegentore auswaerts = ToreUndGegentore.NULL;
	Paarung laufendesSpiel;

	public List<Ergebnis> ergebnisse() {
		return collectToList(ergebnisseStream());
	}

	public List<Ergebnis> ergebnisse(ErgebnisTyp... ergebnisTyp) {
		return collectToList(ergebnisseStream().filter(e -> entryErgebnisIsTypeOf(e, ergebnisTyp)));
	}

	private Stream<ErgebnisEntry> ergebnisseStream() {
		return ergebnisse.stream();
	}

	private static List<Ergebnis> collectToList(Stream<ErgebnisEntry> filter) {
		return filter.map(ErgebnisEntry::ergebnis).toList();
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

	public static class TabellenPlatzBuilder {

		public TabellenPlatzBuilder() {
			ergebnisse = new ArrayList<>();
		}

		public TabellenPlatzBuilder ergebnis(Ergebnis ergebnis, ErgebnisTyp ergebnisTyp) {
			ergebnisse.add(new ErgebnisEntry(ergebnis, ergebnisTyp));
			return this;
		}

		public TabellenPlatzBuilder tore(boolean isSwapped, int toreHeim, int toreGast) {
			ToreUndGegentore toreUndGegentore = new ToreUndGegentore(toreHeim, toreGast);
			return isSwapped ? auswaerts(toreUndGegentore) : heim(toreUndGegentore);
		}

	}

	public int torDifferenz() {
		return tore() - gegentore();
	}

	public TabellenPlatz merge(TabellenPlatz other) {
		return builder() //
				.ergebnisse(mergeLists(ergebnisse, other.ergebnisse)) //
				.spiele(mergeInts(spiele, other.spiele)) //
				.punkte(mergeInts(punkte, other.punkte)) //
				.heim(mergeMergeables(heim, other.heim)) //
				.auswaerts(mergeMergeables(auswaerts, other.auswaerts)) //
				.wappen(lastIfNotNull(wappen, other.wappen)) //
				.laufendesSpiel(lastIfNotNull(laufendesSpiel, other.laufendesSpiel)) //
				.build();
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
		return (int) ergebnisseStream().map(ErgebnisEntry::ergebnis).filter(type::equals).count();
	}

}