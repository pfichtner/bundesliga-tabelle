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

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;
import lombok.ToString;

@Repository
class OpenLigaDbSpieltagRepo implements SpieltagRepo {

	@ToString
	private class Team {
		String teamName;
	}

	@ToString
	private class MatchResult {
		int pointsTeam1;
		int pointsTeam2;
	}

	@ToString
	private class Match {
		Team team1;
		Team team2;
		MatchResult[] matchResults;

		Paarung toDomain() {
			PaarungBuilder b = Paarung.builder().team1(team1.teamName).team2(team2.teamName);
			b = matchResults.length == 0 ? b : b.ergebnis(matchResults[0].pointsTeam1, matchResults[0].pointsTeam2);
			return b.build();
		}
	}

	@Override
	public List<Paarung> lade(String league, String season) throws Exception {
		return Arrays.stream(new Gson().fromJson(readJson(league, season), Match[].class)).map(Match::toDomain)
				.collect(toList());
	}

	protected String readJson(String league, String season) throws Exception {
		return HttpClient.newHttpClient()
				.send(HttpRequest.newBuilder(URI.create(makeUrl(league, season))).build(), BodyHandlers.ofString())
				.body();
	}

	private String makeUrl(String league, String season) {
		return "https://api.openligadb.de/getmatchdata/" + league + "/" + season;
	}

}
