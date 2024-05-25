package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.AUSWAERTS;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.HEIM;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static lombok.AccessLevel.PRIVATE;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.atruvia.ase.samman.buli.domain.Paarung.PaarungView;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz.ErgebnisEntry;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz.TabellenPlatzBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public class Tabelle {

	@Value
	@Accessors(fluent = true)
	private static class OrdnungsElement implements Comparable<OrdnungsElement> {

		// [X] Die nach dem Subtraktionsverfahren ermittelte Tordifferenz
		// [X] Anzahl der erzielten Tore
		// [X] Das Gesamtergebnis aus Hin- und Rückspiel im direkten Vergleich
		// [X] Die Anzahl der auswärts erzielten Tore im direkten Vergleich
		// [X] die Anzahl aller auswärts erzielten Tore

		private static final Comparator<OrdnungsElement> comparator = comparing(value(TabellenPlatz::punkte)) //
				.thenComparing(comparing(value(TabellenPlatz::torDifferenz))) //
				.thenComparing(comparing(value(TabellenPlatz::gesamtTore))) //
				.thenComparing(direkterVegleichGesamt()) //
				.thenComparing(direkterVegleichGesamtAuswaertsTore()) //
				.thenComparing(comparing(value(TabellenPlatz::auswaertsTore))) //
				.reversed();

		private static Comparator<OrdnungsElement> direkterVegleichGesamt() {
			return (o1, o2) -> {
				return Integer.compare( //
						whereToreIs(o1.tabellenPlatz, gegnerIs(o2.tabellenPlatz.identifier())), //
						whereToreIs(o2.tabellenPlatz, gegnerIs(o1.tabellenPlatz.identifier())) //
				);
			};
		}

		private static Comparator<OrdnungsElement> direkterVegleichGesamtAuswaertsTore() {
			return (o1, o2) -> {
				return Integer.compare( //
						whereToreIs(o1.tabellenPlatz, istAuswaerts().and(gegnerIs(o2.tabellenPlatz.identifier()))), //
						whereToreIs(o2.tabellenPlatz, istAuswaerts().and(gegnerIs(o1.tabellenPlatz.identifier()))) //
				);
			};
		}

		private static Predicate<ErgebnisEntry> gegnerIs(Object gegner) {
			return e -> Objects.equals(e.identifierGegner(), gegner);
		}

		private static Predicate<ErgebnisEntry> istAuswaerts() {
			return e -> Objects.equals(e.viewDirection(), AUSWAERTS);
		}

		private static int whereToreIs(TabellenPlatz tabellenPlatz, Predicate<ErgebnisEntry> filter) {
			return tabellenPlatz.ergebnisseEntryStream().filter(filter).map(ErgebnisEntry::tore)
					.mapToInt(Integer::valueOf).sum();
		}

		@Getter(value = PRIVATE)
		TabellenPlatz tabellenPlatz;

		@Override
		public int hashCode() {
			return 0;
		}

		private static <T> Function<OrdnungsElement, T> value(Function<TabellenPlatz, T> function) {
			return t -> function.apply(t.tabellenPlatz);
		}

		@Override
		public boolean equals(Object o) {
			return comparator.compare(this, (OrdnungsElement) o) == 0;
		}

		@Override
		public int compareTo(OrdnungsElement other) {
			return comparator.thenComparing(comparing(e -> e.tabellenPlatz().teamName())).compare(this, other);
		}

	}

	private final Map<Object, TabellenPlatz> eintraege = new HashMap<>();

	public void add(Paarung paarung) {
		addInternal(paarung.viewForTeam(HEIM));
		addInternal(paarung.viewForTeam(AUSWAERTS));
	}

	private void addInternal(PaarungView paarung) {
		eintraege.merge(paarung.team().identifier(), newEntry(paarung), TabellenPlatz::mergeWith);
	}

	private TabellenPlatz newEntry(PaarungView paarung) {
		var team = paarung.team();
		TabellenPlatzBuilder builder = TabellenPlatz.builder().team(team.identifier(), paarung.team().team())
				.wappen(paarung.team().wappen());
		if (!paarung.isGeplant()) {
			builder = builder.spiele(1) //
					.ergebnis(paarung.ergebnis(), paarung.ergebnisTyp(), paarung.direction(), paarung.tore(),
							paarung.gegner().identifier(), paarung.gegentore()) //
					.punkte(paarung.ergebnis().punkte()) //
					.withTore(paarung.direction(), paarung.tore()) //
					.withGegentore(paarung.direction(), paarung.gegentore()) //
					.laufendesSpiel(paarung.isLaufend() ? paarung : null) //
			;
		}

		return builder.build();
	}

	public List<TabellenPlatz> getEntries() {
		// TODO make it side-affect-free, does it work W/O zip!?
		AtomicInteger platz = new AtomicInteger(1);
		Map<OrdnungsElement, List<TabellenPlatz>> platzGruppen = eintraege.values().stream()
				.collect(groupingBy(OrdnungsElement::new));
		return platzGruppen.entrySet().stream() //
				.sorted(Entry.comparingByKey()) //
				.map(Entry::getValue) //
				.flatMap(t -> makeGroup(platz, t)) //
				.toList();
	}

	private static Stream<TabellenPlatz> makeGroup(AtomicInteger platz, List<TabellenPlatz> tabellenPlaetze) {
		int no = platz.getAndAdd(tabellenPlaetze.size());
		return tabellenPlaetze.stream().sorted(comparing(OrdnungsElement::new)).map(tp -> tp.withPlatz(no));
	}

}