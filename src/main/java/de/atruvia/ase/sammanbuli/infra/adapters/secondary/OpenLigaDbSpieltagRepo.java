package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static java.nio.charset.Charset.defaultCharset;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import de.atruvia.ase.samman.buli.infra.DataRetrieveTest.Match;
import de.atruvia.ase.sammanbuli.domain.Paarung;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;

public class OpenLigaDbSpieltagRepo implements SpieltagRepo {

	String url = "https://api.openligadb.de/getmatchdata/bl1/2022";

	public List<Paarung> lade(String league, String season) {
		String content = Files.contentOf(new File(getClass().getClassLoader().getResource("2022.json").toURI()),
				defaultCharset());
		return Arrays.stream(new Gson().fromJson(content, Match[].class)).map(Match::toDomain).collect(toList());
	}

}
