package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import de.atruvia.ase.samman.buli.domain.ports.secondary.TeamRepo;
import lombok.ToString;

@Repository
class OpenLigaDbWappenRepo implements TeamRepo {

	@ToString
	private class TeamInfo {
		String teamName;
		String teamIconUrl;

		URI toDomain() {
			return URI.create(teamIconUrl);
		}

	}

	@Override
	public URI getTeams(String league, String season, String teamName) throws Exception {
		return getTeams(league, season);
	}

	@Override
	public URI getTeams(String league, String season) throws Exception {
		return Arrays.stream(new Gson().fromJson(readJson(league, season), TeamInfo[].class))
				.filter(t -> t.teamName.equals(teamName)).findFirst().map(TeamInfo::toDomain).orElse(null);
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
