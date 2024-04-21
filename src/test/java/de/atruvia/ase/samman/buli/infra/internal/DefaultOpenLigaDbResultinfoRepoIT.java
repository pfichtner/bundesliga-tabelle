package de.atruvia.ase.samman.buli.infra.internal;

import static de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.endergebnisType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.client.RestTemplate;

class DefaultOpenLigaDbResultinfoRepoIT {

	OpenLigaDbResultinfoRepo sut = new DefaultOpenLigaDbResultinfoRepo(new RestTemplate());

	@ParameterizedTest
	@MethodSource("provideLeagueSeasonCombinations")
	void endergebnisHasHighestGlobalId(String league, String season) {
		assertThat(endergebnisType(sut.getResultinfos(league, season)).name).isEqualTo("Endergebnis");
	}

	static List<Arguments> provideLeagueSeasonCombinations() {
		return List.of( //
				arguments("bl1", "2022"), //
				arguments("bl1", "2023"), //
				arguments("bl2", "2022"), //
				arguments("bl2", "2023") //
		);
	}

}
