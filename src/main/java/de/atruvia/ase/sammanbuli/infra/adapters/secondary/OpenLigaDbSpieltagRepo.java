package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import de.atruvia.ase.samman.buli.infra.DataRetrieveTest.Match;
import de.atruvia.ase.samman.buli.infra.DataRetrieveTest.MatchResult;
import de.atruvia.ase.samman.buli.infra.DataRetrieveTest.Team;
import de.atruvia.ase.sammanbuli.domain.Paarung;
import de.atruvia.ase.sammanbuli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;
import lombok.ToString;

public class OpenLigaDbSpieltagRepo implements SpieltagRepo {

	String url = "https://api.openligadb.de/getmatchdata/bl1/2022";

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

	public List<Paarung> lade(String league, String season) {
		String content = Files.contentOf(new File(getClass().getClassLoader().getResource("2022.json").toURI()),
				defaultCharset());
		return Arrays.stream(new Gson().fromJson(content, Match[].class)).map(Match::toDomain).collect(toList());
	}

}
