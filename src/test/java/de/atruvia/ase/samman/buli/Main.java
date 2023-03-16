package de.atruvia.ase.samman.buli;

import java.util.List;

import de.atruvia.ase.sammanbuli.domain.TabellenPlatz;
import de.atruvia.ase.sammanbuli.domain.ports.primary.DefaultTabellenService;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;
import de.atruvia.ase.sammanbuli.infra.adapters.secondary.OpenLigaDbSpieltagRepo;

public class Main {

	public static void main(String[] args) {
		List<TabellenPlatz> erstelleTabelle = new DefaultTabellenService(spieltagRepo()).erstelleTabelle("bl1", "2022");
		for (TabellenPlatz tabellenPlatz : erstelleTabelle) {
			System.out.println(tabellenPlatz);
		}
	}

	private static SpieltagRepo spieltagRepo() {
		return new OpenLigaDbSpieltagRepo();
	}

}
