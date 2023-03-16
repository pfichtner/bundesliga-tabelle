package de.atruvia.ase.sammanbuli.domain.ports.primary;

import java.util.List;

import de.atruvia.ase.sammanbuli.domain.Paarung;
import de.atruvia.ase.sammanbuli.domain.Tabelle;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultTabellenService implements TabellenService {

	private final SpieltagRepo spieltagRepo;

	@Override
	public void erstelleTabelle(String league, String season) {
		Tabelle tabelle = new Tabelle();

		try {
			spieltagRepo.lade(league, season).forEach(tabelle::add);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
