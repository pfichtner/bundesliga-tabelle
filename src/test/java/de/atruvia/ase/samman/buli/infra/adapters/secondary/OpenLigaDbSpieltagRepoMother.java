package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static java.nio.file.Files.readString;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.net.URL;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class OpenLigaDbSpieltagRepoMother {

	public static OpenLigaDbSpieltagRepo spieltagFsRepo() {
		return new OpenLigaDbSpieltagRepo() {
			@Override
			protected String readJson(String league, String season) throws Exception {
				return readString(new File(url("getmatchdata", league, season).toURI()).toPath());
			}
		};
	}

	public static OpenLigaDbWappenRepo wappenFsRepo() {
		return new OpenLigaDbWappenRepo() {
			@Override
			protected String readJson(String league, String season) throws Exception {
				return readString(new File(url("getmatchdata", league, season).toURI()).toPath());
			}
		};
	}

	private static URL url(String base, String league, String season) {
		return OpenLigaDbSpieltagRepoMother.class.getClassLoader()
				.getResource(String.format(base + "/%s/%s.json", league, season));
	}

}
