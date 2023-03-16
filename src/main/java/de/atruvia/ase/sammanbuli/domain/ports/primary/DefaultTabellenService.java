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
	public void erstelleTabelle(String league, String season) throws Exception {
		Tabelle tabelle = new Tabelle();

		List<Paarung> lade = spieltagRepo.lade(league, season);
		lade.forEach(tabelle::add);

		// TODO Auto-generated method stub

	}

}
