package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.AUSWAERTS;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.HEIM;
import static de.atruvia.ase.samman.buli.util.Merger.lastNonNull;
import static de.atruvia.ase.samman.buli.util.Merger.merge;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PRIVATE;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungView;
import de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection;
import de.atruvia.ase.samman.buli.util.Merger.Mergeable;
import lombok.AllArgsConstructor;
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
	@AllArgsConstructor(access = PRIVATE)
	public static class ToreUndGegentore implements Mergeable<ToreUndGegentore> {

		private static final ToreUndGegentore NULL = new ToreUndGegentore(0, 0);

		public static ToreUndGegentore toreUndGegentore(int tore, int gegentore) {
			return new ToreUndGegentore(tore, gegentore);
		}

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
	Map<ViewDirection, ToreUndGegentore> tore;
	PaarungView laufendesSpiel;

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

	ToreUndGegentore heim() {
		return tore.getOrDefault(HEIM, ToreUndGegentore.NULL);
	}

	ToreUndGegentore auswaerts() {
		return tore.getOrDefault(AUSWAERTS, ToreUndGegentore.NULL);
	}

	public int tore() {
		return heim().tore + auswaerts().tore;
	}

	public int gegentore() {
		return heim().gegentore + auswaerts().gegentore;
	}

	public static class TabellenPlatzBuilder {

		public TabellenPlatzBuilder() {
			ergebnisse = new ArrayList<>();
			tore = new HashMap<>();
		}

		public TabellenPlatzBuilder ergebnis(Ergebnis ergebnis, ErgebnisTyp ergebnisTyp) {
			ergebnisse.add(new ErgebnisEntry(ergebnis, ergebnisTyp));
			return this;
		}

		public TabellenPlatzBuilder heim(ToreUndGegentore toreUndGegentore) {
			return toreUndGegentore(HEIM, toreUndGegentore);
		}

		public TabellenPlatzBuilder auswaerts(ToreUndGegentore toreUndGegentore) {
			return toreUndGegentore(AUSWAERTS, toreUndGegentore);
		}

		public TabellenPlatzBuilder toreUndGegentore(ViewDirection viewDirection, ToreUndGegentore toreUndGegentore) {
			tore.put(viewDirection, toreUndGegentore);
			return this;
		}

	}

	public int torDifferenz() {
		return tore() - gegentore();
	}

	public TabellenPlatz mergeWith(TabellenPlatz other) {
		return builder() //
				.team(lastNonNull(team, other.team)) //
				.ergebnisse(merge(ergebnisse, other.ergebnisse)) //
				.spiele(merge(spiele, other.spiele)) //
				.punkte(merge(punkte, other.punkte)) //
				.heim(merge(heim(), other.heim())) //
				.auswaerts(merge(auswaerts(), other.auswaerts())) //
				.wappen(lastNonNull(wappen, other.wappen)) //
				.laufendesSpiel(lastNonNull(laufendesSpiel, other.laufendesSpiel)) //
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