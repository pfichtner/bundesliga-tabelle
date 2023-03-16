package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static java.nio.file.Files.readString;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class OpenLigaDbSpieltagRepoMother {

	public static OpenLigaDbSpieltagRepo readFromLocalFilesystemRepo() {
		return new OpenLigaDbSpieltagRepo() {
			@Override
			protected String readJson(String league, String season) throws Exception {
				return readString(
						extracted(league, season));
			}

			private Path extracted(String league, String season) throws URISyntaxException {
				return new File(getClass().getClassLoader().getResource(league + "/" + season + ".json").toURI())
						.toPath();
			}
		};
	}

}
