package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static java.net.URI.create;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.Entry;
import de.atruvia.ase.samman.buli.infra.internal.AvailableLeagueRepo;
import de.atruvia.ase.samman.buli.infra.internal.DefaultOpenLigaDbResultinfoRepo;

class OpenLigaDbSpieltagRepoIT {

	String teamMuenchen = "FC Bayern München";
	String teamFrankfurt = "Eintracht Frankfurt";
	String teamBremen = "Werder Bremen";

	URI wappenFrankfurt = create("https://i.imgur.com/X8NFkOb.png");
	URI wappenMuenchen = create("https://i.imgur.com/jJEsJrj.png");
	URI wappenBremen = create("https://upload.wikimedia.org/wikipedia/commons/thumb/b/be/"
			+ "SV-Werder-Bremen-Logo.svg/681px-SV-Werder-Bremen-Logo.svg.png");

	@Test
	void canRetrieveDataOf2022() {
		List<Paarung> paarungen = repo().lade("bl1", "2022");
		Paarung expected0 = Paarung.builder() //
				.ergebnisTyp(BEENDET) //
				.heim(new Entry(91, teamFrankfurt, wappenFrankfurt, 1)) //
				.gast(new Entry(40, teamMuenchen, wappenMuenchen, 6)) //
				.build();
		assertThat(paarungen).hasSize(18 / 2 * 17 * 2).element(0).isEqualTo(expected0);
	}

	@Test
	void canRetrieveDataOf2023() {
		List<Paarung> paarungen = repo().lade("bl1", "2023");
		Paarung expected0 = Paarung.builder() //
				.ergebnisTyp(BEENDET) //
				.heim(new Entry(134, teamBremen, wappenBremen, 0)) //
				.gast(new Entry(40, teamMuenchen, wappenMuenchen, 4)) //
				.build();
		assertThat(paarungen).element(0).isEqualTo(expected0);
	}

	OpenLigaDbSpieltagRepo repo() {
		RestTemplate restTemplate = new RestTemplate();
		return new OpenLigaDbSpieltagRepo(restTemplate,
				new DefaultOpenLigaDbResultinfoRepo(restTemplate, new AvailableLeagueRepo(restTemplate)));
	}

}
