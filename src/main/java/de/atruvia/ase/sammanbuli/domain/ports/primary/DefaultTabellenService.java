package de.atruvia.ase.sammanbuli.domain.ports.primary;

import java.util.List;

import de.atruvia.ase.sammanbuli.domain.Tabelle;
import de.atruvia.ase.sammanbuli.domain.TabellenPlatz;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultTabellenService implements TabellenService {

	private final SpieltagRepo spieltagRepo;

	@Override
	public void erstelleTabelle(String league, String season) {
		Tabelle tabelle = new Tabelle();
		setzeSpiele(league, season, tabelle);
		List<TabellenPlatz> entries = tabelle.getEntries();
		for (TabellenPlatz tabellenPlatz : entries) {
			System.out.println(tabellenPlatz);
		}

	}

	private void setzeSpiele(String league, String season, Tabelle tabelle) {
		try {
			spieltagRepo.lade(league, season).forEach(tabelle::add);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
