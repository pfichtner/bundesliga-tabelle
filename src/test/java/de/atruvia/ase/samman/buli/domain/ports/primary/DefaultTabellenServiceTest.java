package de.atruvia.ase.samman.buli.domain.ports.primary;

import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.spieltagFsRepo;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.approvaltests.Approvals.verify;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.TabellenPlatz;

class DefaultTabellenServiceTest {

	@Test
	void tabelleBl12022Spieltag24() {
		TabellenService sut = new DefaultTabellenService(spieltagFsRepo());
		List<TabellenPlatz> erstellteTabelle = sut.erstelleTabelle("bl1", "2022");
		String tabelle = erstellteTabelle.stream().map(f -> print(f, longestTeamName(erstellteTabelle)))
				.collect(joining("\n"));
		verify(tabelle);
	}

	@Test
	void tabelleBl12023Spieltag27_gamesRunning_goalsButFinalResultsAre_0_0() {
		TabellenService sut = new DefaultTabellenService(spieltagFsRepo());
		List<TabellenPlatz> erstellteTabelle = sut.erstelleTabelle("bl1", "2023-games-running");
		String tabelle = erstellteTabelle.stream().map(f -> print(f, longestTeamName(erstellteTabelle)))
				.collect(joining("\n"));
		verify(tabelle);
	}

	@Test
	void tabelleBl12023Spieltag27_gamesRunning_goalsAndFinalResultsAreCorrect() {
		TabellenService sut = new DefaultTabellenService(spieltagFsRepo());
		List<TabellenPlatz> erstellteTabelle = sut.erstelleTabelle("bl1", "2023-games-running-correct-final-result");
		String tabelle = erstellteTabelle.stream().map(f -> print(f, longestTeamName(erstellteTabelle)))
				.collect(joining("\n"));
		verify(tabelle);
	}

	@Test
	void whenRepoThrowsExceptionThenTheServiceThrowsTheException() {
		String message = "some data load error";
		TabellenService sut = new DefaultTabellenService((league, season) -> {
			throw new RuntimeException(message);
		});
		assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> sut.erstelleTabelle("bl1", "2022"))
				.withFailMessage(message);
	}

	int longestTeamName(List<TabellenPlatz> tabellenPlaetze) {
		return tabellenPlaetze.stream().map(TabellenPlatz::team).mapToInt(String::length).max().orElse(0);
	}

	String print(TabellenPlatz tabellenPlatz, int length) {
		return asList( //
				stringFormat(length, tabellenPlatz.team()), //
				tabellenPlatz.spiele(), //
				tabellenPlatz.siege(), //
				tabellenPlatz.unentschieden(), //
				tabellenPlatz.niederlagen(), //
				tabellenPlatz.tore(), //
				tabellenPlatz.gegentore(), //
				tabellenPlatz.torDifferenz(), //
				tabellenPlatz.punkte(), //
				tabellenPlatz.wappen() //
		).stream().map(this::format).collect(joining(" | "));
	}

	String stringFormat(int length, String team) {
		return String.format("%-" + (length + 1) + "s", team);
	}

	String format(Object o) {
		return o instanceof Number ? "%3d".formatted(o) : Objects.toString(o);
	}

}
