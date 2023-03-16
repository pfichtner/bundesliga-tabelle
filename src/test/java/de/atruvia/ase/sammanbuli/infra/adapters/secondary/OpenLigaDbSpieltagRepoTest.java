package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static java.nio.file.Files.readString;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.sammanbuli.domain.Paarung;

class OpenLigaDbSpieltagRepoTest {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		OpenLigaDbSpieltagRepo repo = repo();
		List<Paarung> paarungen = repo.lade("bl1", "2022");
		assertThat(paarungen.get(0)).isEqualTo(
				Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").ergebnis(1, 6).build());
	}

	private OpenLigaDbSpieltagRepo repo() {
		OpenLigaDbSpieltagRepo repo = new OpenLigaDbSpieltagRepo() {
			@Override
			protected String readJson() throws Exception {
				return readString(new File(getClass().getClassLoader().getResource("2022.json").toURI()).toPath());
			}
		};
		return repo;
	}

}
