package de.atruvia.ase.samman.buli.domain.ports.secondary;

import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung;

public interface SpieltagRepo {

	List<Paarung> lade(String league, String season);

}
