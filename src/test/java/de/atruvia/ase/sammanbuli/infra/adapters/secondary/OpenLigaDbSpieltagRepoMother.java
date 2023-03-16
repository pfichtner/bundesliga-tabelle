package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static java.nio.file.Files.readString;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class OpenLigaDbSpieltagRepoMother {

	public static OpenLigaDbSpieltagRepo readFromLocalFilesystemRepo() {
		return new OpenLigaDbSpieltagRepo() {
			@Override
			protected String readJson(String league, String season) throws Exception {
				return readString(new File(url(league, season).toURI()).toPath());
			}

		};
	}

	private static URL url(String league, String season) {
		return getClass().getClassLoader().getResource(league + "/" + season + ".json");
	}

}
