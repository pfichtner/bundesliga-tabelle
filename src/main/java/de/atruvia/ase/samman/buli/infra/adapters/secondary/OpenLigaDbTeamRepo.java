package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static java.util.Arrays.stream;
import static lombok.AccessLevel.PUBLIC;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import de.atruvia.ase.samman.buli.domain.Team;
import de.atruvia.ase.samman.buli.domain.ports.secondary.TeamRepo;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Repository
@RequiredArgsConstructor
class OpenLigaDbTeamRepo implements TeamRepo {

	private static final String LEAGUE = "league";
	private static final String SEASON = "season";

	private static final String SERVICE_URI = "https://api.openligadb.de/getavailableteams/{" + LEAGUE + "}/{" + SEASON
			+ "}";

	private final RestTemplate restTemplate;

	@ToString
	@FieldDefaults(level = PUBLIC)
	private static class JsonTeam {
		String teamName;
		String teamIconUrl;

		Team toDomain() {
			return Team.builder().name(teamName).wappen(toURI(teamIconUrl)).build();
		}

		private static URI toURI(String wappen) {
			return wappen == null ? null : URI.create(wappen);
		}

	}

	@Override
	public List<Team> getTeams(String league, String season) throws Exception {
		return stream(restTemplate.getForObject(SERVICE_URI, JsonTeam[].class, Map.of(LEAGUE, league, SEASON, season)))
				.map(JsonTeam::toDomain).toList();
	}

}
