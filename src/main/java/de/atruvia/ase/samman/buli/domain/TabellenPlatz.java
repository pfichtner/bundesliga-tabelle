package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.AUSWAERTS;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.HEIM;
import static de.atruvia.ase.samman.buli.util.Merger.lastNonNull;
import static de.atruvia.ase.samman.buli.util.Merger.merge;
import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungView;
import de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection;
import de.atruvia.ase.samman.buli.util.Merger.Mergeable;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@Builder
public class TabellenPlatz implements Mergeable<TabellenPlatz> {

	@Value
	private static class ErgebnisEntry {
		Ergebnis ergebnis;
		ErgebnisTyp ergebnisTyp;
	}

	private static final BinaryOperator<Integer> adder = (i1, i2) -> i1 + i2;

	URI wappen;
	@With
	int platz;
	@With
	String team;
	int spiele;
	List<ErgebnisEntry> ergebnisse;
	int punkte;
	Map<ViewDirection, Integer> tore;
	Map<ViewDirection, Integer> gegentore;
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

	public int gesamtTore() {
		return heimtore() + auswaertsTore();
	}

	public int gesamtGegentore() {
		return heimGegentore() + auswaertsGegentore();
	}

	public int heimtore() {
		return tore().getOrDefault(HEIM, 0);
	}

	public int auswaertsTore() {
		return tore().getOrDefault(AUSWAERTS, 0);
	}

	public int heimGegentore() {
		return gegentore().getOrDefault(HEIM, 0);
	}

	public int auswaertsGegentore() {
		return gegentore().getOrDefault(AUSWAERTS, 0);
	}

	public int torDifferenz() {
		return gesamtTore() - gesamtGegentore();
	}

	public TabellenPlatzBuilder toBuilder() {
		TabellenPlatzBuilder builder = new TabellenPlatzBuilder();
		builder.wappen = wappen;
		builder.platz = platz;
		builder.team = team;
		builder.spiele = spiele;
		builder.ergebnisse = new ArrayList<>(ergebnisse);
		builder.punkte = punkte;
		builder.tore = new HashMap<>(tore);
		builder.gegentore = new HashMap<>(gegentore);
		builder.laufendesSpiel = laufendesSpiel;
		return builder;
	}

	public static class TabellenPlatzBuilder {

		public TabellenPlatzBuilder() {
			ergebnisse = new ArrayList<>();
			tore = new HashMap<>();
			gegentore = new HashMap<>();
		}

		public TabellenPlatzBuilder ergebnis(Ergebnis ergebnis, ErgebnisTyp ergebnisTyp) {
			ergebnisse.add(new ErgebnisEntry(ergebnis, ergebnisTyp));
			return this;
		}

		public TabellenPlatzBuilder withGegentore(ViewDirection direction, int anzahl) {
			gegentore.put(direction, anzahl);
			return this;
		}

		public TabellenPlatzBuilder withTore(ViewDirection viewDirection, int anzahl) {
			tore.put(viewDirection, anzahl);
			return this;
		}

	}

	@Override
	public TabellenPlatz mergeWith(TabellenPlatz other) {
		return builder() //
				.team(lastNonNull(team, other.team)) //
				.ergebnisse(merge(ergebnisse, other.ergebnisse)) //
				.spiele(merge(spiele, other.spiele)) //
				.punkte(merge(punkte, other.punkte)) //
				.tore(merge(adder, tore, other.tore)) //
				.gegentore(merge(adder, gegentore, other.gegentore)) //
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

	public Ergebnis[] tendenz() {
		return last(5, ergebnisse(BEENDET)).toArray(Ergebnis[]::new);
	}

	private static <T> Stream<T> last(int count, List<T> elements) {
		int size = elements.size();
		return range(0, count).mapToObj(i -> {
			int index = size - i - 1;
			return index >= 0 ? elements.get(index) : null;
		});
	}

}