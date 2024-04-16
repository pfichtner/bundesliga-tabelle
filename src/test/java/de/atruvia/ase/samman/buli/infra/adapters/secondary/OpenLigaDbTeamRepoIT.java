package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static java.net.URI.create;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import de.atruvia.ase.samman.buli.domain.Team;

class OpenLigaDbTeamRepoIT {

	String teamMuenchen = "FC Bayern München";
	String teamFrankfurt = "Eintracht Frankfurt";

	URI wappenFrankfurt = create("https://i.imgur.com/X8NFkOb.png");
	URI wappenMuenchen = create("https://i.imgur.com/jJEsJrj.png");

	@Test
	void canRetrieveDataOf2022() throws Exception {
		String league = "bl1";
		String season = "2022";
		assertThat(team(teamFrankfurt, league, season))
				.hasValueSatisfying(t -> assertThat(t.getWappen()).isEqualTo(wappenFrankfurt));
		assertThat(team(teamMuenchen, league, season))
				.hasValueSatisfying(t -> assertThat(t.getWappen()).isEqualTo(wappenMuenchen));
	}

	@Test
	void canRetrieveDataOf2023() throws Exception {
		String league = "bl1";
		String season = "2023";
		assertThat(team(teamFrankfurt, league, season))
				.hasValueSatisfying(t -> assertThat(t.getWappen()).isEqualTo(wappenFrankfurt));
		assertThat(team(teamMuenchen, league, season))
				.hasValueSatisfying(t -> assertThat(t.getWappen()).isEqualTo(wappenMuenchen));
	}

	Optional<Team> team(String teamName, String league, String season) throws Exception {
		return teams(league, season).filter(t -> t.getName().equals(teamName)).findFirst();
	}

	Stream<Team> teams(String league, String season) throws Exception {
		return repo().getTeams(league, season).stream();
	}

	OpenLigaDbTeamRepo repo() {
		return new OpenLigaDbTeamRepo(new RestTemplate());
	}

}
