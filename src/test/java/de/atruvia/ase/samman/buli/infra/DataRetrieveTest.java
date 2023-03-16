package de.atruvia.ase.samman.buli.infra;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import de.atruvia.ase.samman.Paarung;
import de.atruvia.ase.samman.Paarung.PaarungBuilder;
import lombok.ToString;

class DataRetrieveTest {

	@ToString
	class Team {
		String teamName;
	}

	@ToString
	class MatchResult {
		int pointsTeam1;
		int pointsTeam2;
	}

	@ToString
	class Match {
		Team team1;
		Team team2;
		MatchResult[] matchResults;

		Paarung toDomain() {
			System.out.println(this);
			PaarungBuilder team22 = Paarung.builder().team1(team1.teamName).team2(team2.teamName);
			return team22
					.ergebnis(matchResults[0].pointsTeam1, matchResults[0].pointsTeam2).build();
		}
	}

	@Test
	void canRetrieveDataOf2022() throws IOException, InterruptedException, URISyntaxException {
		String url = "https://api.openligadb.de/getmatchdata/bl1/2022";
		List<Paarung> paarungen = lade(url);
		assertThat(paarungen.get(0)).isEqualTo(
				Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").ergebnis(1, 6).build());

	}

	private List<Paarung> lade(String url) throws IOException, InterruptedException, URISyntaxException {
		String content = Files.contentOf(new File(getClass().getClassLoader().getResource("2022.json").toURI()),
				defaultCharset());

		return Arrays.stream(new Gson().fromJson(content, Match[].class)).map(Match::toDomain).collect(toList());
	}

}
