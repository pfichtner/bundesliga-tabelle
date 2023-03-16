package de.atruvia.ase.sammanbuli.domain;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import de.atruvia.ase.sammanbuli.domain.TabelleTest.OrdnungsElement;

class Tabelle {

	private final Map<String, TabellenPlatz> eintraege = new HashMap<>();

	void add(Paarung paarung) {
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
				.map(Entry::getValue).flatMap(t -> t.stream().sorted(comparing(OrdnungsElement::new))
						.map(tp -> tp.withPlatz(platz.get())))
				.collect(toList());
	}

	private TabellenPlatz setTeam(Entry<String, TabellenPlatz> entry) {
		return entry.getValue().withTeam(entry.getKey());
	}

}