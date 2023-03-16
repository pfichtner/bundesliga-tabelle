package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.sammanbuli.domain.Paarung;

class OpenLigaDbSpieltagRepoTest {

	@Test
	void canRetrieveDataOf2022() throws IOException, InterruptedException, URISyntaxException {
		OpenLigaDbSpieltagRepo repo = new OpenLigaDbSpieltagRepo();
		String url = "https://api.openligadb.de/getmatchdata/bl1/2022";
		List<Paarung> paarungen = repo.lade("bl1", "2022");
		assertThat(paarungen.get(0)).isEqualTo(
				Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").ergebnis(1, 6).build());
	}

}
