package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static java.nio.file.Files.readString;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class OpenLigaDbSpieltagRepoMother {

	public static OpenLigaDbSpieltagRepo readFromLocalFilesystemRepo() {
		return new OpenLigaDbSpieltagRepo() {
			@Override
			protected String readJson(String league, String season) throws Exception {
				return readString(
						new File(getClass().getClassLoader().getResource(league + "/" + season + ".json").toURI())
								.toPath());
			}
		};
	}

}
