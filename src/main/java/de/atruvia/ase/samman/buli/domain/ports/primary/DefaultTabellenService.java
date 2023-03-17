package de.atruvia.ase.samman.buli.domain.ports.primary;

import java.util.List;

import org.springframework.stereotype.Service;

import de.atruvia.ase.samman.buli.domain.Tabelle;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DefaultTabellenService implements TabellenService {

	private final SpieltagRepo spieltagRepo;

	@Override
	public List<TabellenPlatz> erstelleTabelle(String league, String season) {
		Tabelle tabelle = new Tabelle();
		setzeSpiele(league, season, tabelle);
		return tabelle.getEntries();
	}

	private void setzeSpiele(String league, String season, Tabelle tabelle) {
		try {
			spieltagRepo.lade(league, season).forEach(tabelle::add);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
