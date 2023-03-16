package de.atruvia.ase.samman.buli;

import de.atruvia.ase.sammanbuli.domain.TabellenPlatz;
import de.atruvia.ase.sammanbuli.domain.ports.primary.DefaultTabellenService;
import de.atruvia.ase.sammanbuli.domain.ports.primary.TabellenService;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;
import de.atruvia.ase.sammanbuli.infra.adapters.secondary.OpenLigaDbSpieltagRepo;

public class Main {

	public static void main(String[] args) {
		for (TabellenPlatz tabellenPlatz : new DefaultTabellenService(spieltagRepo()).erstelleTabelle("bl1", "2022")) {
			System.out.println(tabellenPlatz);
		}
	}

	private static SpieltagRepo spieltagRepo() {
		return new OpenLigaDbSpieltagRepo();
	}

}
