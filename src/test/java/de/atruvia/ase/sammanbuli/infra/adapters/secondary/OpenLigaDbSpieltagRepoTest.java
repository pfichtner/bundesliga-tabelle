package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static de.atruvia.ase.sammanbuli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.readFromLocalFilesystemRepo;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.Paarung;

class OpenLigaDbSpieltagRepoTest {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		List<Paarung> paarungen = readFromLocalFilesystemRepo().lade("bl1", "2022");
		assertThat(paarungen).hasSize(306).element(0).isEqualTo(
				Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").ergebnis(1, 6).build());
	}

}
