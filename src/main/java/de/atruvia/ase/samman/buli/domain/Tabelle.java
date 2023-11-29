package de.atruvia.ase.samman.buli.domain;

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

@RequiredArgsConstructor
public class Tabelle {

	@RequiredArgsConstructor
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
				e -> e.getPunkte(), //
				e -> e.getTorDifferenz(), //
				e -> e.getTore(), //
				e -> e.getToreAuswaerts() //
		);

		private static final Function<OrdnungsElement, TabellenPlatz> getTabellenPlatz = OrdnungsElement::getTabellenPlatz;
		private static final List<Function<OrdnungsElement, Comparable<?>>> extractors = comparators.stream()
				.map(t -> getTabellenPlatz.andThen(t)).toList();

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
			return comparator.thenComparing(comparing(e -> e.tabellenPlatz.getTeam())).compare(this, other);
		}

	}

	private final Map<String, TabellenPlatz> eintraege = new HashMap<>();

	public void add(Paarung paarung) {
		addInternal(paarung, false);
		addInternal(paarung.swap(), true);
	}

	private void addInternal(Paarung paarung, boolean swapped) {
		eintraege.merge(paarung.getTeamHeim(), newEntry(paarung, swapped), TabellenPlatz::merge);
	}

	private TabellenPlatz newEntry(Paarung paarung, boolean swapped) {
		if (!paarung.isGespielt()) {
			return TabellenPlatz.NULL.withWappen(paarung.getWappenHeim());
		}
		Ergebnis ergebnis = paarung.ergebnis();
		TabellenPlatzBuilder builder = TabellenPlatz.builder() //
				.wappen(paarung.getWappenHeim()) //
				.ergebnis(ergebnis) //
				.punkte(punkte(ergebnis));
		int toreTeamHeim = paarung.getToreTeamHeim();
		int toreTeamGast = paarung.getToreTeamGast();
		return (swapped //
				? builder.toreAuswaerts(toreTeamHeim).gegentoreAuswaerts(toreTeamGast) //
				: builder.toreHeim(toreTeamHeim).gegentoreHeim(toreTeamGast) //
		).build();
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
				.map(this::setTeam) //
				.collect(groupingBy(OrdnungsElement::new)) //
		;
		return platzGruppen.entrySet().stream() //
				.sorted(comparing(Entry::getKey)) //
				.map(Entry::getValue) //
				.flatMap(t -> makeGroup(platz, t)) //
				.toList();
	}

	private static Stream<TabellenPlatz> makeGroup(AtomicInteger platz, List<TabellenPlatz> tabellenPlaetze) {
		int no = platz.getAndAdd(tabellenPlaetze.size());
		return tabellenPlaetze.stream().sorted(comparing(OrdnungsElement::new)).map(tp -> tp.withPlatz(no));
	}

	private TabellenPlatz setTeam(Entry<String, TabellenPlatz> entry) {
		return entry.getValue().withTeam(entry.getKey());
	}

}