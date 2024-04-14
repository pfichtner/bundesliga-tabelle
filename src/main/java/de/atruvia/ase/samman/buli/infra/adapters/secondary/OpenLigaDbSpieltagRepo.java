package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEGONNEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.GEPLANT;
import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepo.MatchResult.endergebnis;
import static de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.getEndergebnisType;
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
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Repository
@RequiredArgsConstructor
public class OpenLigaDbSpieltagRepo implements SpieltagRepo {

	private static final String URI_FORMAT = "https://api.openligadb.de/getmatchdata/%s/%s";

	private final Gson gson = new Gson();
	private final HttpClient httpClient = HttpClient.newHttpClient();

	private final OpenLigaDbResultinfoRepo resultinfoRepo;

	@ToString
	private static class Team {
		String teamName;
		String teamIconUrl;
	}

	@ToString
	public static class MatchResult {
		int resultTypeID;
		int pointsTeam1;
		int pointsTeam2;

		public static Optional<MatchResult> endergebnis(MatchResult[] matchResults, List<Resultinfo> resultinfos) {
			return stream(matchResults)
					.filter(t -> t.resultTypeID == getEndergebnisType(resultinfos).globalResultInfo.id)
					.reduce(toOnlyElement());
		}

		private static <T> BinaryOperator<T> toOnlyElement() {
			return (f, s) -> {
				throw new IllegalStateException("Expected at most one element but found at least " + f + " and " + s);
			};
		}

	}

	@ToString
	private static class Goal {

		static final Comparator<Goal> inChronologicalOrder = comparing(g -> g.goalID);
		static final Goal NULL = new Goal();

		int goalID;
		int scoreTeam1;
		int scoreTeam2;
	}

	@ToString
	private static class Match {
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
				MatchResult endergebnis = endergebnis(matchResults, resultinfos)
						.orElseThrow(() -> new IllegalStateException("No final result found in finished game " + this));
				builder.ergebnis(ergebnisTyp, endergebnis.pointsTeam1, endergebnis.pointsTeam2);
			} else if (ergebnisTyp == BEGONNEN) {
				// a final result is always present on started games, but in some cases it has
				// been 0:0 while there have already been shoot some goals. Of course we always
				// could take the "goals" in account (this always is correct) but we should
				// prefer using the final result if it's present.
				Goal latestGoal = latestGoal().orElse(Goal.NULL);
				builder = builder.ergebnis(ergebnisTyp, latestGoal.scoreTeam1, latestGoal.scoreTeam2);
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

		private Optional<Goal> latestGoal() {
			return stream(goals).sorted(Goal.inChronologicalOrder).reduce(lastElement());
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
		return HttpRequest.newBuilder(create(URI_FORMAT.formatted(league, season))).build();
	}

}
