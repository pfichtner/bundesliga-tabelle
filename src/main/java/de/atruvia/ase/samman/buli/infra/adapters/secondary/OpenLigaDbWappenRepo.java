package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import de.atruvia.ase.samman.buli.domain.ports.secondary.TeamRepo;
import lombok.ToString;

@Repository
class OpenLigaDbWappenRepo implements TeamRepo {

	@ToString
	private class Team {
		String teamName;
		String teamIconUrl;

		de.atruvia.ase.samman.buli.domain.Team toDomain() {
			return URI.create(teamIconUrl);
		}

	}

	@Override
	public List<Team> getTeams(String league, String season) throws Exception {
		return Arrays.stream(new Gson().fromJson(readJson(league, season), Team[].class)).map(Team::toDomain)
				.collect(Collectors.toList())
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
