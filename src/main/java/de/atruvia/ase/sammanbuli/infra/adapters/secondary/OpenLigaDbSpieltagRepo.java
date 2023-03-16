package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import java.util.List;

import de.atruvia.ase.sammanbuli.domain.Paarung;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;

public class OpenLigaDbSpieltagRepo implements SpieltagRepo {

	String url = "https://api.openligadb.de/getmatchdata/bl1/2022";

	public List<Paarung> lade(String league, String season) {
		// TODO Auto-generated method stub
		return null;
	}

}
