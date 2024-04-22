package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.teamFsRepo;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.Team;

class OpenLigaDbTeamRepoTest {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		assertTeamHasWappen("Eintracht Frankfurt", "https://i.imgur.com/X8NFkOb.png");
		assertTeamHasWappen("FC Bayern München", "https://i.imgur.com/jJEsJrj.png");
	}

	void assertTeamHasWappen(String teamName, String wappenUri) throws Exception {
		List<Team> teams = repo().getTeams("bl1", "2022");
		assertThat(teams.stream().filter(t -> teamName.equals(t.name())).findFirst())
				.hasValueSatisfying(t -> assertThat(t.wappen()).isEqualTo(URI.create(wappenUri)));
	}

	OpenLigaDbTeamRepo repo() {
		return teamFsRepo();
	}

}
