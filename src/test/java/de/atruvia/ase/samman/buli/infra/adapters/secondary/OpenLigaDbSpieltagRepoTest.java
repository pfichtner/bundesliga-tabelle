package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.resultinfoProvider;
import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.spieltagFsRepo;
import static java.net.URI.create;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
		Paarung expected0 = Paarung.builder() //
				.heim(new Paarung.Entry(teamFrankfurt, wappenFrankfurt)) //
				.gast(new Paarung.Entry(teamMuenchen, wappenMuenchen)) //
				.build().withErgebnis(1, 6);
		assertThat(paarungen).hasSize(306).element(0).isEqualTo(expected0);
	}

	@Test
	void canRetrieveDataOf2023() throws Exception {
		// 2022: "endergebnis" was first element of array, 2023 it was last -> filter
		// "resultTypeID" = 2 for now
		List<Paarung> paarungen = repo().lade("bl1", "2023");
		Paarung expected0 = Paarung.builder() //
				.heim(new Paarung.Entry(teamBremen, wappenBremen)) //
				.gast(new Paarung.Entry(teamMuenchen, wappenMuenchen)) //
				.build().withErgebnis(0, 4);
		assertThat(paarungen).hasSize(9).element(0).isEqualTo(expected0);
	}

	@Test
	void throwsExceptionIfThereAreMatchesWithMultipleFinalResults() throws Exception {
		OpenLigaDbSpieltagRepo repo = new OpenLigaDbSpieltagRepo(resultinfoProvider()) {
			@Override
			protected String readJson(String league, String season) throws Exception {
				return """
						[
						  {
							"team1": {
							  "teamName": "Team 1",
							  "teamIconUrl": "teamIconUrl1"
							},
							"team2": {
							  "teamName": "Team 2",
							  "teamIconUrl": "teamIconUrl2"
							},
							"matchIsFinished": true,
						    "matchResults": [
						      {
						        "resultTypeID": 2
						      },
						      {
						        "resultTypeID": 2
						      }
						    ]
						  }
						 ]
						""";
			}

		};
		assertThatThrownBy(() -> repo.lade("any", "any")).hasMessageContaining("at most one element");
	}

	OpenLigaDbSpieltagRepo repo() {
		return spieltagFsRepo();
	}

}
