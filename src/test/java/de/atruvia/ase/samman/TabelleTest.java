package de.atruvia.ase.samman;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import lombok.Builder;
import lombok.Value;

class TabelleTest {

	@Value
	@Builder
	private static class TabellenPlatz {
		int platz;
		String team;
		int punkte;
		int tore;
	}

	@Value
	@Builder
	private static class Paarung {
		String team1, team2;
	}

	private Paarung[] paarungen;
	private TabellenPlatz[] tabelle;

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(tabellenplatz().platz(1).team("Team 1"), tabellenplatz().platz(2).team("Team 2"));
	}

	private de.atruvia.ase.samman.TabelleTest.Paarung.PaarungBuilder paarung(String team1, String team2) {
		return Paarung.builder().team1(team1).team2(team2);
	}

	private de.atruvia.ase.samman.TabelleTest.TabellenPlatz.TabellenPlatzBuilder tabellenplatz() {
		return TabellenPlatz.builder();
	}

	private void gegenSeienDiePaarungen(Paarung.PaarungBuilder... paarungen) {
		this.paarungen = Arrays.stream(paarungen).map(Paarung.PaarungBuilder::build).toArray(Paarung[]::new);
	}

	private void wennDieTabelleBerechnetWird() {
		tabelle = new TabellenPlatz[] { //
				tabellenplatz().platz(1).team(this.paarungen[0].team1).build(), //
				tabellenplatz().platz(2).team(this.paarungen[0].team2).build() //
		};
	}

	private void dannIstDieTabelle(TabellenPlatz.TabellenPlatzBuilder... expected) {
		assertThat(tabelle).isEqualTo(
				Arrays.stream(expected).map(TabellenPlatz.TabellenPlatzBuilder::build).toArray(TabellenPlatz[]::new));
	}

}
