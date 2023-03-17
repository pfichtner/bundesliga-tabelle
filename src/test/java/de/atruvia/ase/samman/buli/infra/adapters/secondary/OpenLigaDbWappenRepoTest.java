package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.teamFsRepo;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

class OpenLigaDbWappenRepoTest {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		assertTeamHasWappen("Eintracht Frankfurt", "https://i.imgur.com/X8NFkOb.png");
		assertTeamHasWappen("FC Bayern München", "https://i.imgur.com/jJEsJrj.png");
	}

	private void assertTeamHasWappen(String teamName, String str) throws Exception {
		assertThat(teamFsRepo().getTeams("bl1", "2022")).isEqualTo(URI.create(str));
	}

}
