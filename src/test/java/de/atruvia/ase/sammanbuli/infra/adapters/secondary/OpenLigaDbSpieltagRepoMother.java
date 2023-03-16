package de.atruvia.ase.sammanbuli.infra.adapters.secondary;

import static java.nio.file.Files.readString;

import java.io.File;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenLigaDbSpieltagRepoMother {

	OpenLigaDbSpieltagRepo readFromLocalFilesystemRepo() {
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
