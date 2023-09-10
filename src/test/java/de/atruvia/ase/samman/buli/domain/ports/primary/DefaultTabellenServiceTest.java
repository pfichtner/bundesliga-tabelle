package de.atruvia.ase.samman.buli.domain.ports.primary;

import static de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder.paarung;
import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.spieltagFsRepo;
import static java.util.stream.Collectors.joining;
import static org.approvaltests.Approvals.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService.Tabelle;

class DefaultTabellenServiceTest {

	@Test
	void tabelleBl12022Spieltag24() {
		TabellenService sut = new DefaultTabellenService(spieltagFsRepo());
		Tabelle tabelle = sut.erstelleTabelle("bl1", "2022");
		assertThat(tabelle.name()).isEqualTo("1. Fußball-Bundesliga 2022/2023");
		List<TabellenPlatz> plaetze = tabelle.eintraege();
		verify(plaetze.stream().map(f -> print(f, longestTeamName(plaetze))).collect(joining("\n")));
	}

	@Test
	void saisonNameIstDerLetzteNonNullNameDerSpiele() {
		TabellenService sut = new DefaultTabellenService((league, season) -> Arrays.asList( //
				paarung("Team 1", "Team 2").saison("Name 1"), //
				paarung("Team 2", "Team 1").saison("Name 2"), //
				paarung("Team 1", "Team 2").saison(null) //
		).stream().map(PaarungBuilder::build).toList());
		assertThat(sut.erstelleTabelle("", "").name()).isEqualTo("Name 2");
	}

	private int longestTeamName(List<TabellenPlatz> tabellenPlaetze) {
		return tabellenPlaetze.stream().map(TabellenPlatz::getTeam).mapToInt(String::length).max().orElse(0);
	}

	private String print(TabellenPlatz tabellenPlatz, int length) {
		return Arrays
				.asList(stringFormat(length, tabellenPlatz.getTeam()), tabellenPlatz.getSpiele(),
						tabellenPlatz.getAnzahlSiege(), tabellenPlatz.getAnzahlUnentschieden(),
						tabellenPlatz.getAnzahlNiederlagen(), tabellenPlatz.getTore(), tabellenPlatz.getGegentore(),
						tabellenPlatz.getTorDifferenz(), tabellenPlatz.getPunkte(),
						firstCharOf(tabellenPlatz.getLetzte(5)), tabellenPlatz.getWappen())
				.stream().map(this::format).collect(joining(" | "));
	}

	private String firstCharOf(List<Ergebnis> ergebnisse) {
		return ergebnisse.stream().map(e -> e.name().substring(0, 1)).collect(joining());
	}

	private String stringFormat(int length, String team) {
		return String.format("%-" + (length + 1) + "s", team);
	}

	private String format(Object o) {
		return o instanceof Number ? String.format("%3d", o) : Objects.toString(o);
	}

}
