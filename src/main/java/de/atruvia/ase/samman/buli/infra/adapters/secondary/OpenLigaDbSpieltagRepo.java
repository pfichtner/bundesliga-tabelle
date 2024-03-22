package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static java.lang.String.format;
import static java.net.URI.create;
import static java.util.Arrays.stream;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.Entry;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;
import lombok.ToString;

@Repository
class OpenLigaDbSpieltagRepo implements SpieltagRepo {

	private static final String URI_FORMAT = "https://api.openligadb.de/getmatchdata/%s/%s";

	private final Gson gson = new Gson();
	private final HttpClient httpClient = HttpClient.newHttpClient();

	@ToString
	private class Team {
		String teamName;
		String teamIconUrl;
	}

	@ToString
	private class MatchResult {

		private static final int RESULTTYPEID_ENDERGEBNIS = 2;

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

		private Paarung toDomain() {
			PaarungBuilder builder = Paarung.builder() //
					.heim(new Entry(team1.teamName, create(team1.teamIconUrl), 0)) //
					.gast(new Entry(team2.teamName, create(team2.teamIconUrl), 0)) //
			;
			return setFinalResult(builder, matchResults).build();
		}

		private static PaarungBuilder setFinalResult(PaarungBuilder builder, MatchResult[] matchResults) {
			return endergebnis(matchResults).map(r -> builder.ergebnis(r.pointsTeam1, r.pointsTeam2)).orElse(builder);
		}

		private static Optional<MatchResult> endergebnis(MatchResult[] matchResults) {
			return stream(matchResults).filter(MatchResult::isEndergebnis).reduce(toOnlyElement());
		}

		private static <T> BinaryOperator<T> toOnlyElement() {
			return (f, s) -> {
				throw new IllegalStateException("Expected at most one element but found at least " + f + " and " + s);
			};
		}

	}

	@Override
	public List<Paarung> lade(String league, String season) throws Exception {
		return stream(gson.fromJson(readJson(league, season), Match[].class)).map(Match::toDomain).toList();
	}

	protected String readJson(String league, String season) throws Exception {
		return httpClient.send(request(league, season), BodyHandlers.ofString()).body();
	}

	private static HttpRequest request(String league, String season) {
		return HttpRequest.newBuilder(create(format(URI_FORMAT, league, season))).build();
	}

}
