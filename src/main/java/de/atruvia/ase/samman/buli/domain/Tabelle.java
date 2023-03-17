package de.atruvia.ase.samman.buli.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import de.atruvia.ase.samman.buli.domain.ports.secondary.WappenRepository;
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

		private static final Comparator<OrdnungsElement> comparator = comparing(
				(OrdnungsElement e) -> e.tabellenPlatz.getPunkte())
				.thenComparing(comparing(e -> e.tabellenPlatz.getTorDifferenz()))
				.thenComparing(comparing(e -> e.tabellenPlatz.getTore()))
				.thenComparing(comparing(e -> e.tabellenPlatz.getToreAuswaerts())) //
				.reversed();

		private final TabellenPlatz tabellenPlatz;

		@Override
		public int hashCode() {
			return Objects.hash(tabellenPlatz.getPunkte(), tabellenPlatz.getTorDifferenz(), tabellenPlatz.getTore(),
					tabellenPlatz.getToreAuswaerts());
		}

		@Override
		public boolean equals(Object other) {
			return this == other || (other != null && getClass() == other.getClass()
					&& comparator.compare(this, (OrdnungsElement) other) == 0);
		}

		@Override
		public int compareTo(OrdnungsElement other) {
			return comparator.thenComparing(comparing(e -> e.tabellenPlatz.getTeam())).compare(this, other);
		}

	}

	private final Map<String, TabellenPlatz> eintraege = new HashMap<>();
	private final WappenRepository wappenRepository;

	public void add(Paarung paarung) {
		addInternal(paarung, false);
		addInternal(paarung.swap(), true);
	}

	private void addInternal(Paarung paarung, boolean swapped) {
		eintraege.merge(paarung.getTeam1(), newEintrag(paarung, swapped), TabellenPlatz::merge);
	}

	private TabellenPlatz newEintrag(Paarung paarung, boolean swapped) {
		if (!paarung.isGespielt()) {
			return TabellenPlatz.NULL;
		}
		TabellenPlatz.TabellenPlatzBuilder builder = TabellenPlatz.builder() //
				.ergebnis(paarung.ergebnis()) //
				.punkte(paarung.punkte());
		if (swapped) {
			return builder //
					.toreAuswaerts(paarung.getTore()) //
					.gegentoreAuswaerts(paarung.getGegentore()) //
					.build();
		} else
			return builder //
					.toreHeim(paarung.getTore()) //
					.gegentoreHeim(paarung.getGegentore()) //
					.build();
	}

	public List<TabellenPlatz> getEntries() {
		// TODO make it side-affect-free, does it work W/O zip!?
		AtomicInteger platz = new AtomicInteger();
		Map<OrdnungsElement, List<TabellenPlatz>> platzGruppen = eintraege.entrySet().stream().map(this::setTeam)
				.collect(groupingBy(OrdnungsElement::new));
		return platzGruppen.entrySet().stream().sorted(comparing(Entry::getKey)).peek(e -> platz.incrementAndGet())
				.map(Entry::getValue)
				.flatMap(t -> t.stream().sorted(comparing(OrdnungsElement::new)).map(tp -> tp.withPlatz(platz.get()))
						.map(tp -> tp.withWappen(wappen(tp))))
				.collect(toList());
	}

	private URI wappen(TabellenPlatz tp) {
		try {
			return wappenRepository.getWappen("bl1", "2022", tp.getTeam());
		} catch (Exception e) {
			// TODO wenn das Wappen nicht geladen werden kann -> loggen, aber weitermachen
			return null;
		}
	}

	private TabellenPlatz setTeam(Entry<String, TabellenPlatz> entry) {
		return entry.getValue().withTeam(entry.getKey());
	}

}