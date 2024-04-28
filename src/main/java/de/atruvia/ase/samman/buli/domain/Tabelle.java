package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.LAUFEND;
import static java.util.Arrays.asList;
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
import java.util.stream.Stream;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz.TabellenPlatzBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public class Tabelle {

	@RequiredArgsConstructor
	@Accessors(fluent = true)
	@ToString
	private static class OrdnungsElement implements Comparable<OrdnungsElement> {

//	    MIS: Head-to-head goal difference: The goal difference in the matches played between the tied teams.
//	    MIS: Head-to-head away goals scored: The total number of away goals scored by the tied teams in the matches played between them.
//	    HIT: Overall goal difference: The difference between the number of goals scored and the number of goals conceded in all matches played.
//	    HIT: Overall goals scored: The total number of goals scored in all matches played.
//	    HIT: Overall away goals scored: The total number of goals scored by the tied teams in matches played away from their home stadium.
//	    MIS: Overall away goal difference: The difference between the number of away goals scored and the number of away goals conceded by the tied teams in matches played away from their home stadium.
//		If two or more teams have the same rank in the Bundesliga and there is no other criteria that can be used to separate them, then the teams will be listed in alphabetical order according to their full club name.

		private static final List<Function<TabellenPlatz, Comparable<?>>> comparators = asList( //
				TabellenPlatz::punkte, //
				TabellenPlatz::torDifferenz, //
				TabellenPlatz::tore, //
				e -> e.auswaerts().tore() //
		);

		private static final Function<OrdnungsElement, TabellenPlatz> getTabellenPlatz = OrdnungsElement::tabellenPlatz;
		private static final List<Function<OrdnungsElement, Comparable<?>>> extractors = comparators.stream()
				.map(getTabellenPlatz::andThen).toList();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static final Comparator<OrdnungsElement> comparator = extractors.stream() //
				.map(f -> comparing((Function) f)) //
				.reduce(Comparator::thenComparing).orElseThrow().reversed();

		@Getter(value = PRIVATE)
		private final TabellenPlatz tabellenPlatz;

		@Override
		public int hashCode() {
			return extractors.stream().map(e -> e.apply(this)).map(Object::hashCode).reduce(1,
					(h1, h2) -> 31 * h1 + h2);
		}

		@Override
		public boolean equals(Object other) {
			return this == other || (other != null && getClass() == other.getClass() && extractors.stream()
					.allMatch(e -> Objects.equals(e.apply(this), e.apply((OrdnungsElement) other))));
		}

		@Override
		public int compareTo(OrdnungsElement other) {
			return comparator.thenComparing(comparing(e -> e.tabellenPlatz().team())).compare(this, other);
		}

	}

	private final Map<String, TabellenPlatz> eintraege = new HashMap<>();

	public void add(Paarung paarung) {
		addInternal(paarung);
		addInternal(paarung.swap());
	}

	private void addInternal(Paarung paarung) {
		eintraege.merge(paarung.teamHeim(), newEntry(paarung).build(), TabellenPlatz::merge);
	}

	private TabellenPlatzBuilder newEntry(Paarung paarung) {
		TabellenPlatzBuilder builder = TabellenPlatz.builder().wappen(paarung.wappenHeim());
		return paarung.hatErgebnis() ? withErgebnis(builder, paarung) : builder;
	}

	private static TabellenPlatzBuilder withErgebnis(TabellenPlatzBuilder builder, Paarung paarung) {
		Ergebnis ergebnis = paarung.ergebnis();
		return builder //
				.spiele(1) //
				.ergebnis(ergebnis, paarung.ergebnisTyp()) //
				.punkte(punkte(ergebnis)) //
				.tore(paarung.isSwapped(), paarung.toreHeim(), paarung.toreGast()) //
				.laufendesSpiel(paarung.ergebnisTypIs(LAUFEND) ? paarung : null);
	}

	private static int punkte(Ergebnis ergebnis) {
		return switch (ergebnis) {
		case SIEG -> 3;
		case UNENTSCHIEDEN -> 1;
		case NIEDERLAGE -> 0;
		};
	}

	public List<TabellenPlatz> getEntries() {
		// TODO make it side-affect-free, does it work W/O zip!?
		AtomicInteger platz = new AtomicInteger(1);
		Map<OrdnungsElement, List<TabellenPlatz>> platzGruppen = eintraege.entrySet().stream() //
				.map(Tabelle::setTeam) //
				.collect(groupingBy(OrdnungsElement::new)) //
		;
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

	private static TabellenPlatz setTeam(Entry<String, TabellenPlatz> entry) {
		return entry.getValue().withTeam(entry.getKey());
	}

}