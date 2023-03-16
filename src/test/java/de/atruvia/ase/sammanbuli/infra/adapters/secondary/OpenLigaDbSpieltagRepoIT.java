package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.sammanbuli.domain.Paarung;

class OpenLigaDbSpieltagRepoIT {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		List<Paarung> paarungen = repo().lade("bl1", "2022");
		assertThat(paarungen.get(0)).isEqualTo(
				Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").ergebnis(1, 6).build());
	}

	private OpenLigaDbSpieltagRepo repo() {
		return new OpenLigaDbSpieltagRepo();
	}

}
