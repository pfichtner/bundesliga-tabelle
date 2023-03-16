package de.atruvia.ase.samman;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

class DataRetrieveTest {

	@Test
	void canRetrieveDataOf2022() {
		String url = "https://api.openligadb.de/getmatchdata/bl1/2022";
		List<Paarung> paarungen = lade(url);
		assertThat(paarungen.get(0)).isEqualTo(
				Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").ergebnis(1, 6).build());

	}

	private List<Paarung> lade(String url) throws IOException, InterruptedException {
		URI uri = URI.create(url);
		HttpRequest request = HttpRequest.newBuilder(uri).build();
		String content = HttpClient.newHttpClient().send(request, BodyHandlers.ofString()).body();

		class Match {

		}

		Match[] fromJson = new Gson().fromJson(content, Match[].class);

		return Arrays.stream(fromJson).map(match -> Paarung.builder().team1("Eintracht Frankfurt")
				.team2("FC Bayern München").ergebnis(1, 6).build()).collect(Collectors.toList());
	}

}
