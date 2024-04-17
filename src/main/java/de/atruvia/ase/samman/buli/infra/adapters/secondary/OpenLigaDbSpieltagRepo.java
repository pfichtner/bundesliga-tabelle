package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEGONNEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.GEPLANT;
import static de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.getEndergebnisType;
import static java.net.URI.create;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static lombok.AccessLevel.PUBLIC;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.Entry;
import de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Repository
@RequiredArgsConstructor
public class OpenLigaDbSpieltagRepo implements SpieltagRepo {

	private static final String LEAGUE = "league";
	private static final String SEASON = "season";

	private static final String SERVICE_URI = "https://api.openligadb.de/getmatchdata/{" + LEAGUE + "}/{" + SEASON
			+ "}";

	private final RestTemplate restTemplate;
	private final OpenLigaDbResultinfoRepo resultinfoRepo;

	@ToString
	@FieldDefaults(level = PUBLIC)
	private static class Team {
		String teamName;
		String teamIconUrl;
	}

	@ToString
	@FieldDefaults(level = PUBLIC)
	private static class MatchResult {
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
	@FieldDefaults(level = PUBLIC)
	private static class Goal {

		static final Comparator<Goal> inChronologicalOrder = comparing(g -> g.goalID);
		static final Goal NULL = new Goal();

		int goalID;
		int scoreTeam1;
		int scoreTeam2;
	}

	@ToString
	@FieldDefaults(level = PUBLIC)
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
				MatchResult endergebnis = MatchResult.endergebnis(matchResults, resultinfos)
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
		return stream(restTemplate.getForObject(SERVICE_URI, Match[].class, Map.of(LEAGUE, league, SEASON, season)))
				.map(t -> t.toDomain(resultinfos)).toList();
	}

}
