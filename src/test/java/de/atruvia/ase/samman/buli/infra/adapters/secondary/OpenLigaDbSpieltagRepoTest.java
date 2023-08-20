package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.spieltagFsRepo;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.Paarung;

class OpenLigaDbSpieltagRepoTest {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		List<Paarung> paarungen = repo().lade("bl1", "2022");
		assertThat(paarungen).hasSize(306).element(0)
				.isEqualTo(Paarung.builder().teamHeim("Eintracht Frankfurt").teamGast("FC Bayern München").ergebnis(1, 6)
						.wappen1(wappenFrankfurt()).wappen2(wappenMuenchen()).build());
	}

	@Test
	void canRetrieveDataOf2023() throws Exception {
		// 2022: "endergebnis" was first element of array, 2023 it was last -> filter
		// "resultTypeID" = 2 for now
		List<Paarung> paarungen = repo().lade("bl1", "2023");
		assertThat(paarungen).hasSize(9).element(0).isEqualTo(Paarung.builder().teamHeim("Werder Bremen")
				.teamGast("FC Bayern München").ergebnis(0, 4).wappen1(wappenBremen()).wappen2(wappenMuenchen()).build());
	}

	URI wappenFrankfurt() {
		return URI.create("https://i.imgur.com/X8NFkOb.png");
	}

	URI wappenMuenchen() {
		return URI.create("https://i.imgur.com/jJEsJrj.png");
	}

	URI wappenBremen() {
		return URI.create(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/SV-Werder-Bremen-Logo.svg/681px-SV-Werder-Bremen-Logo.svg.png");
	}

	OpenLigaDbSpieltagRepo repo() {
		return spieltagFsRepo();
	}

}
