package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

class OpenLigaDbTeamRepoIT {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		assertTeamHasWappen("Eintracht Frankfurt", "https://i.imgur.com/X8NFkOb.png");
		assertTeamHasWappen("FC Bayern München", "https://i.imgur.com/jJEsJrj.png");
	}

	void assertTeamHasWappen(String teamName, String wappenUri) throws Exception {
		assertThat(repo().getTeams("bl1", "2022").stream().filter(t -> t.getName().equals(teamName)).findFirst())
				.hasValueSatisfying(t -> assertThat(t.getWappen()).isEqualTo(URI.create(wappenUri)));
	}

	private OpenLigaDbTeamRepo repo() {
		return new OpenLigaDbTeamRepo();
	}

}
