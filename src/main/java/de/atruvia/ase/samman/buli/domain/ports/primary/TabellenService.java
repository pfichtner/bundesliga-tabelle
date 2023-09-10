package de.atruvia.ase.samman.buli.domain.ports.primary;

import java.util.List;

import de.atruvia.ase.samman.buli.domain.TabellenPlatz;

public interface TabellenService {

	public interface Tabelle {
		String name();

		List<TabellenPlatz> eintraege();
	}

	Tabelle erstelleTabelle(String league, String season);

}
