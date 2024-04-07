package de.atruvia.ase.samman.buli.infra.internal;

import static de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.getEndergebnisType;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class OpenLigaDbResultinfoRepoIT {

	OpenLigaDbResultinfoRepo sut = new OpenLigaDbResultinfoRepo();

	@ParameterizedTest
	@MethodSource("provideLeagueSeasonCombinations")
	void endergebnisHasHighestGlobalId(String league, String season) {
		assertThat(getEndergebnisType(sut.getResultinfos(league, season)).name).isEqualTo("Endergebnis");
	}

	static Stream<String[]> provideLeagueSeasonCombinations() {
		return Stream.of( //
				new String[] { "bl1", "2022" }, //
				new String[] { "bl1", "2023" }, //
				new String[] { "bl2", "2022" }, //
				new String[] { "bl2", "2023" } //
		);
	}

}
