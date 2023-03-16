package de.atruvia.ase.sammanbuli.domain.ports.primary;

import java.util.List;

import de.atruvia.ase.sammanbuli.domain.TabellenPlatz;

public interface TabellenService {

	List<TabellenPlatz> erstelleTabelle(String league, String season);

}
