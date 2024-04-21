package de.atruvia.ase.samman.buli.infra.internal;

import static de.atruvia.ase.samman.buli.springframework.RestTemplateMock.configureMock;
import static java.nio.file.Files.readString;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpRequest;
import org.springframework.web.client.RestTemplate;

import de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother;
import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo;

class DefaultOpenLigaDbResultinfoRepoTest {

	private static final String ENDERGEBNIS = "Endergebnis";

	OpenLigaDbResultinfoRepo sut = new DefaultOpenLigaDbResultinfoRepo(configureMock(new RestTemplate(), r -> {
		try {
			URL resource = requireNonNull(OpenLigaDbSpieltagRepoMother.class.getClassLoader()
					.getResource(openLigaDbResultinfoRepoResource(r)), () -> "No response captured for " + r);
			return readString(new File(resource.toURI()).toPath());
		} catch (IOException | URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}));

	private static String openLigaDbResultinfoRepoResource(HttpRequest request) {
		String uri = request.getURI().toASCIIString();
		String[] parts = uri.split("/");
		if ("getavailableleagues".equals(parts[parts.length - 1])) {
			return "getavailableleagues/getavailableleagues.json";
		} else if ("getresultinfos".equals(parts[parts.length - 2])) {
			return "getresultinfos/%s.json".formatted(parts[parts.length - 1]);
		}
		throw new IllegalStateException("Cannot handle " + uri);
	}

	@Test
	void endergebnisType2022() {
		assertSoftly(s -> {
			Resultinfo endergebnisType = endergebnisType("bl1", "2022");
			s.assertThat(endergebnisType.id).isEqualTo(5337);
			s.assertThat(endergebnisType.name).isEqualTo(ENDERGEBNIS);
		});
	}

	@Test
	void endergebnisType2023() {
		assertSoftly(s -> {
			Resultinfo endergebnisType = endergebnisType("bl1", "2023");
			s.assertThat(endergebnisType.id).isEqualTo(5413);
			s.assertThat(endergebnisType.name).isEqualTo(ENDERGEBNIS);
		});
	}

	private Resultinfo endergebnisType(String league, String season) {
		return Resultinfo.getEndergebnisType(sut.getResultinfos(league, season));
	}

}
