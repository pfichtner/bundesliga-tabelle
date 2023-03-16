package de.atruvia.ase.samman.buli.infra;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import de.atruvia.ase.samman.Paarung;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

class DataRetrieveTest {

	@Test
	void canRetrieveDataOf2022() throws IOException, InterruptedException {
		String url = "https://api.openligadb.de/getmatchdata/bl1/2022";
		List<Paarung> paarungen = lade(url);
		assertThat(paarungen.get(0)).isEqualTo(
				Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").ergebnis(1, 6).build());

	}

	private List<Paarung> lade(String url) throws IOException, InterruptedException, URISyntaxException {
		String content = Files.contentOf(new File(getClass().getClassLoader().getResource("2022.json").toURI()),
				Charset.defaultCharset());

		@ToString
		@FieldDefaults(level = PUBLIC)
		class Team {
			@SerializedName("teamName")
			String teamName;
		}
		@ToString
		@FieldDefaults(level = PUBLIC)
		class MatchResult {
			@SerializedName("pointsTeam1")
			int pointsTeam1;
			@SerializedName("pointsTeam2")
			int pointsTeam2;
		}
		@ToString
		@FieldDefaults(level = PUBLIC)
		class Match {
			@SerializedName("team1")
			Team team1;
			@SerializedName("team2")
			Team team2;
			MatchResult[] matchResults;

			Paarung toDomain() {
				System.out.println(this);
				return Paarung.builder().team1(team1.teamName).team2(team2.teamName)
						.ergebnis(matchResults[0].pointsTeam1, matchResults[0].pointsTeam2).build();
			}
		}

		return Arrays.stream(new Gson().fromJson(content, Match[].class)).peek(System.out::println).map(Match::toDomain)
				.collect(toList());
	}

}
