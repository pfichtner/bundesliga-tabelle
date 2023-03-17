package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.wappenFsRepo;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

class OpenLigaDbWappenRepoTest {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		assertTeamHasWappen("Eintracht Frankfurt", "https://i.imgur.com/X8NFkOb.png");
		assertThat(wappenFsRepo().getWappen("bl1", "2022", "FC Bayern München"))
				.isEqualTo(URI.create("https://i.imgur.com/jJEsJrj.png"));
	}

	private void assertTeamHasWappen(String teamName, String str) throws Exception {
		assertThat(wappenFsRepo().getWappen("bl1", "2022", teamName))
				.isEqualTo(URI.create(str));
	}

}
