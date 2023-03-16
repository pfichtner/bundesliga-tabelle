package de.atruvia.ase.samman;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import lombok.ToString;

class DataRetrieveIT {

	@Test
	void canRetrieveDataOf2022() throws IOException, InterruptedException {
		String url = "https://api.openligadb.de/getmatchdata/bl1/2022";
		List<Paarung> paarungen = lade(url);
		assertThat(paarungen.get(0)).isEqualTo(
				Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").ergebnis(1, 6).build());

	}

	private List<Paarung> lade(String url) throws IOException, InterruptedException {
		URI uri = URI.create(url);
		HttpRequest request = HttpRequest.newBuilder(uri).build();
		String content = HttpClient.newHttpClient().send(request, BodyHandlers.ofString()).body();

		@ToString
		class Team {
			String teamName;
		}
		@ToString
		class MatchResult {
//			String resultName;
			int pointsTeam1, pointsTeam2;
		}
		@ToString
		class Match {
			Team team1, team2;
			MatchResult[] matchResults;

			Paarung toDomain() {
				return Paarung.builder().team1(team1.teamName).team2(team2.teamName)
						.ergebnis(matchResults[0].pointsTeam1, matchResults[0].pointsTeam2).build();
			}
		}

		return Arrays.stream(new Gson().fromJson(content, Match[].class)).map(Match::toDomain).collect(toList());
	}

}
