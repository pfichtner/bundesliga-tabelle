package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static java.util.Arrays.stream;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
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
		String teamIconUrl;
	}

	@ToString
	private class MatchResult {
		static final int RESULTTYPEID_ENDERGEBNIS = 2;

		boolean isEndergebnis() {
			return resultTypeID == RESULTTYPEID_ENDERGEBNIS;
		}

		int resultTypeID;
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
			b = b.wappen1(URI.create(team1.teamIconUrl)).wappen2(URI.create(team2.teamIconUrl));
			b = setFinalResult(b, matchResults);
			return b.build();
		}

		private static PaarungBuilder setFinalResult(PaarungBuilder builder, MatchResult[] matchResults) {
			return stream(matchResults).filter(MatchResult::isEndergebnis).findFirst()
					.map(r -> builder.ergebnis(r.pointsTeam1, r.pointsTeam2)).orElse(builder);
		}

	}

	@Override
	public List<Paarung> lade(String league, String season) throws Exception {
		return stream(new Gson().fromJson(readJson(league, season), Match[].class)).map(Match::toDomain).toList();
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
