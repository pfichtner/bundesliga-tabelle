package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.spieltagFsRepo;
import static java.net.URI.create;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.Paarung;

class OpenLigaDbSpieltagRepoTest {

	String teamFrankfurt = "Eintracht Frankfurt";
	String teamMuenchen = "FC Bayern München";
	String teamBremen = "Werder Bremen";

	URI wappenFrankfurt = create("https://i.imgur.com/X8NFkOb.png");
	URI wappenMuenchen = create("https://i.imgur.com/jJEsJrj.png");
	URI wappenBremen = create("https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/"
			+ "SV-Werder-Bremen-Logo.svg/681px-SV-Werder-Bremen-Logo.svg.png");

	@Test
	void canRetrieveDataOf2022() throws Exception {
		List<Paarung> paarungen = repo().lade("bl1", "2022");
		assertThat(paarungen).hasSize(306).element(0).isEqualTo(Paarung.builder().teamHeim(teamFrankfurt)
				.teamGast(teamMuenchen).ergebnis(1, 6).wappenHeim(wappenFrankfurt).wappenGast(wappenMuenchen).build());
	}

	@Test
	void canRetrieveDataOf2023() throws Exception {
		// 2022: "endergebnis" was first element of array, 2023 it was last -> filter
		// "resultTypeID" = 2 for now
		List<Paarung> paarungen = repo().lade("bl1", "2023");
		assertThat(paarungen).hasSize(9).element(0).isEqualTo(Paarung.builder().teamHeim(teamBremen)
				.teamGast(teamMuenchen).ergebnis(0, 4).wappenHeim(wappenBremen).wappenGast(wappenMuenchen).build());
	}

	OpenLigaDbSpieltagRepo repo() {
		return spieltagFsRepo();
	}

}
