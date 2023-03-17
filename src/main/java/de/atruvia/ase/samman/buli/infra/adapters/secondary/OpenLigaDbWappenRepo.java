package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import de.atruvia.ase.samman.buli.domain.Team;
import de.atruvia.ase.samman.buli.domain.ports.secondary.TeamRepo;
import lombok.ToString;

@Repository
class OpenLigaDbWappenRepo implements TeamRepo {

	@ToString
	private class JsonTeam {
		String teamName;
		String teamIconUrl;

		Team toDomain() {
			return Team.builder().name(teamName).wappen(teamIconUrl == null ? null : URI.create(teamIconUrl)).build();
		}

	}

	@Override
	public List<Team> getTeams(String league, String season) throws Exception {
		return Arrays.stream(new Gson().fromJson(readJson(league, season), JsonTeam[].class)).map(JsonTeam::toDomain)
				.collect(toList());
	}

	protected String readJson(String league, String season) throws Exception {
		return HttpClient.newHttpClient()
				.send(HttpRequest.newBuilder(URI.create(makeUrl(league, season))).build(), BodyHandlers.ofString())
				.body();
	}

	private String makeUrl(String league, String season) {
		return "https://api.openligadb.de/getavailableteams/" + league + "/" + season;
	}

}
