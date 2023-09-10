package de.atruvia.ase.samman.buli.domain.ports.primary;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.TabellenRechner;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Service
@RequiredArgsConstructor
class DefaultTabellenService implements TabellenService {

	@Value
	@Builder
	@Accessors(fluent = true)
	private static class DefaultTabelle implements Tabelle {
		String name;
		List<TabellenPlatz> eintraege;
	}

	private final SpieltagRepo spieltagRepo;

	@Override
	public Tabelle erstelleTabelle(String league, String season) {
		TabellenRechner tabellenRechner = new TabellenRechner();
		List<Paarung> paarungen = lade(league, season);
		paarungen.forEach(tabellenRechner::add);
		return new DefaultTabelle(lastSaisonName(paarungen), tabellenRechner.getEntries());
	}

	private String lastSaisonName(List<Paarung> paarungen) {
		return paarungen.stream().map(Paarung::getSaison).filter(Objects::nonNull).reduce((a, b) -> b).orElse("");
	}

	private List<Paarung> lade(String league, String season) {
		try {
			return spieltagRepo.lade(league, season);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
