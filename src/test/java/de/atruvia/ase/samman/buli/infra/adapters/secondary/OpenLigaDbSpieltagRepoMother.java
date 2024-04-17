package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.springframework.RestTemplateMock.configureMock;
import static java.nio.file.Files.readString;
import static lombok.AccessLevel.PRIVATE;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.GlobalResultInfo;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class OpenLigaDbSpieltagRepoMother {

	public static OpenLigaDbSpieltagRepo spieltagFsRepo() {
		return new OpenLigaDbSpieltagRepo(configureMock(new RestTemplate(), r -> {
			String[] parts = r.getURI().toASCIIString().split("/");
			try {
				return readString(path("getmatchdata", parts[parts.length - 2], parts[parts.length - 1]));
			} catch (IOException | URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}), resultinfoProvider());
	}

	public static OpenLigaDbResultinfoRepo resultinfoProvider() {
		return new OpenLigaDbResultinfoRepo(null) {
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
		return new OpenLigaDbTeamRepo(configureMock(new RestTemplate(), r -> {
			String[] parts = r.getURI().toASCIIString().split("/");
			try {
				return readString(path("getavailableteams", parts[parts.length - 2], parts[parts.length - 1]));
			} catch (IOException | URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}));
	}

	private static Path path(String base, String league, String season) throws URISyntaxException {
		return new File(url(base, league, season).toURI()).toPath();
	}

	private static URL url(String base, String league, String season) {
		return OpenLigaDbSpieltagRepoMother.class.getClassLoader()
				.getResource(base + "/%s/%s.json".formatted(league, season));
	}

}
