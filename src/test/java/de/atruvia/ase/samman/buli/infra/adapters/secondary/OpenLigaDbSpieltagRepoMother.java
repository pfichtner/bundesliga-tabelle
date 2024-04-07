package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static java.nio.file.Files.readString;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.GlobalResultInfo;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class OpenLigaDbSpieltagRepoMother {

	public static OpenLigaDbSpieltagRepo spieltagFsRepo() {
		return new OpenLigaDbSpieltagRepo(resultinfoProvider()) {
			@Override
			protected String readJson(String league, String season) throws Exception {
				return readString(path("getmatchdata", league, season));
			}
		};
	}

	public static OpenLigaDbResultinfoRepo resultinfoProvider() {
		return new OpenLigaDbResultinfoRepo() {
			@Override
			public List<Resultinfo> getResultinfos(String league, String season) {
				return List.of(resultinfo());
			}
		};
	}

	private static Resultinfo resultinfo() {
		Resultinfo resultinfo = new Resultinfo();
		resultinfo.orderId = 2;
		GlobalResultInfo globalResultInfo = new GlobalResultInfo();
		globalResultInfo.id = 2;
		resultinfo.globalResultInfo = globalResultInfo;
		return resultinfo;
	}

	public static OpenLigaDbTeamRepo teamFsRepo() {
		return new OpenLigaDbTeamRepo() {
			@Override
			protected String readJson(String league, String season) throws Exception {
				return readString(path("getavailableteams", league, season));
			}
		};
	}

	private static Path path(String base, String league, String season) throws URISyntaxException {
		return new File(url(base, league, season).toURI()).toPath();
	}

	private static URL url(String base, String league, String season) {
		return OpenLigaDbSpieltagRepoMother.class.getClassLoader()
				.getResource(String.format(base + "/%s/%s.json", league, season));
	}

}
