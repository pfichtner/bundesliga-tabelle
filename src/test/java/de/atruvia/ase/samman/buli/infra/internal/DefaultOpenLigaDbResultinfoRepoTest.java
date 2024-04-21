package de.atruvia.ase.samman.buli.infra.internal;

import static de.atruvia.ase.samman.buli.springframework.ResponseFromResourcesSupplier.responseFromResources;
import static de.atruvia.ase.samman.buli.springframework.RestTemplateMock.configureMock;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo;

class DefaultOpenLigaDbResultinfoRepoTest {

	private static final String ENDERGEBNIS = "Endergebnis";

	OpenLigaDbResultinfoRepo sut = new DefaultOpenLigaDbResultinfoRepo(
			configureMock(new RestTemplate(), responseFromResources(DefaultOpenLigaDbResultinfoRepoTest::resolve)));

	private static String resolve(String[] parts) {
		if ("getavailableleagues".equals(parts[parts.length - 1])) {
			return "getavailableleagues/getavailableleagues.json";
		} else if ("getresultinfos".equals(parts[parts.length - 2])) {
			return "getresultinfos/%s.json".formatted(parts[parts.length - 1]);
		}
		return null;
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
		return Resultinfo.endergebnisType(sut.getResultinfos(league, season));
	}

}
