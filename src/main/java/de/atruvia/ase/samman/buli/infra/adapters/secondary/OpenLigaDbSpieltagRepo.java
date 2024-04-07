package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEGONNEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.GEPLANT;
import static java.lang.String.format;
import static java.net.URI.create;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.Entry;
import de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;
import de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbResultinfoRepo.Resultinfo;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Repository
@RequiredArgsConstructor
class OpenLigaDbSpieltagRepo implements SpieltagRepo {

	private static final String URI_FORMAT = "https://api.openligadb.de/getmatchdata/%s/%s";

	private final Gson gson = new Gson();
	private final HttpClient httpClient = HttpClient.newHttpClient();

	private final OpenLigaDbResultinfoRepo resultinfoRepo;

	@ToString
	private class Team {
		String teamName;
		String teamIconUrl;
	}

	@ToString
	private class MatchResult {
		int resultTypeID;
		int pointsTeam1;
		int pointsTeam2;
	}

	@ToString
	private class Goal {
		static Comparator<Goal> inChronologicalOrder = comparing(g -> g.goalID);
		int goalID;
		int scoreTeam1;
		int scoreTeam2;
	}

	@ToString
	private class Match {
		Team team1;
		Team team2;
		boolean matchIsFinished;
		MatchResult[] matchResults;
		Goal[] goals;

		private Paarung toDomain(List<Resultinfo> resultinfos) {
			PaarungBuilder builder = Paarung.builder() //
					.heim(new Entry(team1.teamName, create(team1.teamIconUrl))) //
					.gast(new Entry(team2.teamName, create(team2.teamIconUrl))) //
			;
			ErgebnisTyp ergebnisTyp = ergebnisTyp();
			if (ergebnisTyp == BEENDET) {
				MatchResult endergebnis = endergebnis(resultinfos)
						.orElseThrow(() -> new IllegalStateException("No final result found in finished game " + this));
				builder.ergebnis(ergebnisTyp, endergebnis.pointsTeam1, endergebnis.pointsTeam2);
			} else if (ergebnisTyp == BEGONNEN) {
				// a final result is always present on started games, but in some cases it has
				// been 0:0 while there have already been shoot some goals. Of course we always
				// could take the "goals" in account (this always is correct) but we should
				// prefer using the final result if it's present.
				Optional<MatchResult> endergebnisWithScore = endergebnis(resultinfos)
						.filter(e -> e.pointsTeam1 > 0 && e.pointsTeam2 > 0);
				if (endergebnisWithScore.isPresent()) {
					builder = builder.ergebnis(ergebnisTyp, endergebnisWithScore.get().pointsTeam1,
							endergebnisWithScore.get().pointsTeam2);
				} else {
					Goal lastGoal = lastGoal().orElseGet(() -> new Goal());
					builder = builder.ergebnis(ergebnisTyp, lastGoal.scoreTeam1, lastGoal.scoreTeam2);
				}
			}
			return builder.build();
		}

		private ErgebnisTyp ergebnisTyp() {
			if (matchIsFinished) {
				return BEENDET;
			} else if (matchResults.length > 0) {
				return BEGONNEN;
			} else {
				return GEPLANT;
			}
		}

		private Optional<MatchResult> endergebnis(List<Resultinfo> resultinfos) {
			Resultinfo last = resultinfos.get(resultinfos.size() - 1);
			return stream(matchResults).filter(t -> t.resultTypeID == last.globalResultInfo.id).reduce(toOnlyElement());
		}

		private Optional<Goal> lastGoal() {
			return stream(goals).sorted(Goal.inChronologicalOrder).reduce(lastElement());
		}

		private static <T> BinaryOperator<T> toOnlyElement() {
			return (f, s) -> {
				throw new IllegalStateException("Expected at most one element but found at least " + f + " and " + s);
			};
		}

	}

	private static <T> BinaryOperator<T> lastElement() {
		return (f, s) -> s;
	}

	@Override
	public List<Paarung> lade(String league, String season) throws Exception {
		List<Resultinfo> resultinfos = resultinfoRepo.getResultinfos(league, season);
		return stream(gson.fromJson(readJson(league, season), Match[].class)).map(t -> t.toDomain(resultinfos))
				.toList();
	}

	protected String readJson(String league, String season) throws Exception {
		return httpClient.send(request(league, season), BodyHandlers.ofString()).body();
	}

	private static HttpRequest request(String league, String season) {
		return HttpRequest.newBuilder(create(format(URI_FORMAT, league, season))).build();
	}

}
