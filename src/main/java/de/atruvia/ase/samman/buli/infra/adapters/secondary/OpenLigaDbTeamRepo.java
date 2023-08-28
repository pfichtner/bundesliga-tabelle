package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static java.lang.String.format;
import static java.net.URI.create;
import static java.util.Arrays.stream;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import de.atruvia.ase.samman.buli.domain.Team;
import de.atruvia.ase.samman.buli.domain.ports.secondary.TeamRepo;
import lombok.ToString;

@Repository
class OpenLigaDbTeamRepo implements TeamRepo {

	private static final String URI_FORMAT = "https://api.openligadb.de/getavailableteams/%s/%s";

	private final Gson gson = new Gson();
	private final HttpClient httpClient = HttpClient.newHttpClient();

	@ToString
	private class JsonTeam {
		String teamName;
		String teamIconUrl;

		Team toDomain() {
			return Team.builder().name(teamName).wappen(teamIconUrl == null ? null : create(teamIconUrl)).build();
		}

	}

	@Override
	public List<Team> getTeams(String league, String season) throws Exception {
		return stream(gson.fromJson(readJson(league, season), JsonTeam[].class)).map(JsonTeam::toDomain).toList();
	}

	protected String readJson(String league, String season) throws Exception {
		return httpClient.send(request(league, season), BodyHandlers.ofString()).body();
	}

	private HttpRequest request(String league, String season) {
		return HttpRequest.newBuilder(create(format(URI_FORMAT, league, season))).build();
	}

}
