package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.Paarung;

class OpenLigaDbSpieltagRepoIT {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		List<Paarung> paarungen = repo().lade("bl1", "2022");
		assertThat(paarungen).element(0)
				.isEqualTo(Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").ergebnis(1, 6)
						.wappen1(URI.create("https://i.imgur.com/X8NFkOb.png"))
						.wappen2(URI.create("https://i.imgur.com/jJEsJrj.png")));
	}

	private OpenLigaDbSpieltagRepo repo() {
		return new OpenLigaDbSpieltagRepo();
	}

}
