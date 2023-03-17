package de.atruvia.ase.samman.buli;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.atruvia.ase.sammanbuli.domain.ports.primary.DefaultTabellenService;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;
import de.atruvia.ase.sammanbuli.infra.adapters.secondary.OpenLigaDbSpieltagRepo;

@SpringBootApplication
public class Main {

	public static void main(String[] args) {
		new DefaultTabellenService(spieltagRepo()).erstelleTabelle("bl1", "2022").forEach(System.out::println);
	}

	private static SpieltagRepo spieltagRepo() {
		return new OpenLigaDbSpieltagRepo();
	}

}
