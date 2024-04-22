package de.atruvia.ase.samman.buli.infra.internal;

import static de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.endergebnisType;
import static de.atruvia.ase.samman.buli.springframework.ResponseFromResourcesSupplier.responseFromResources;
import static de.atruvia.ase.samman.buli.springframework.RestTemplateMock.restTemplateMock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo;

class DefaultOpenLigaDbResultinfoRepoTest {

	static final String ENDERGEBNIS = "Endergebnis";

	OpenLigaDbResultinfoRepo sut = new DefaultOpenLigaDbResultinfoRepo(
			restTemplateMock(responseFromResources(DefaultOpenLigaDbResultinfoRepoTest::resolve)));

	static String resolve(String[] parts) {
		if ("getavailableleagues".equals(parts[parts.length - 1])) {
			return "getavailableleagues/getavailableleagues.json";
		} else if ("getresultinfos".equals(parts[parts.length - 2])) {
			return "getresultinfos/%s.json".formatted(parts[parts.length - 1]);
		}
		return null;
	}

	@Test
	void endergebnisType2022() {
		List<Resultinfo> resultinfos = sut.getResultinfos("bl1", "2022");
		assertSoftly(s -> {
			assertThat(resultinfos).satisfiesExactly( //
					r -> {
						s.assertThat(r).isSameAs(endergebnisType(resultinfos));
						s.assertThat(r.id).isEqualTo(5337);
						s.assertThat(r.name).isEqualTo(ENDERGEBNIS);
					}, r -> {
						s.assertThat(r.id).isEqualTo(5338);
						s.assertThat(r.name).isEqualTo("Halbzeit");
					});
		});
	}

	@Test
	void endergebnisType2023() {
		List<Resultinfo> resultinfos = sut.getResultinfos("bl1", "2023");
		assertSoftly(s -> {
			assertThat(resultinfos).satisfiesExactly( //
					r -> {
						s.assertThat(r).isSameAs(endergebnisType(resultinfos));
						s.assertThat(r.id).isEqualTo(5413);
						s.assertThat(r.name).isEqualTo(ENDERGEBNIS);
					}, r -> {
						s.assertThat(r.id).isEqualTo(5456);
						s.assertThat(r.name).isEqualTo("Halbzeitergebnis");
					});
		});

	}

}
