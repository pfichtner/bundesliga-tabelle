package de.atruvia.ase.samman;

import static org.assertj.core.api.Assertions.assertThat;

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

	private String[] teams;
	private TabellenPlatz[] tabelle;

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegebenSeien("Team1", "Team2");
		gegenSeienErgebisse();
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(TabellenPlatz.builder().platz(1).team("Team 1").build(),
				TabellenPlatz.builder().platz(2).team("Team 2").build());
	}

	private void gegebenSeien(String... teams) {
		this.teams = teams;
	}

	private void gegenSeienErgebisse() {
		// TODO Auto-generated method stub
	}

	private void wennDieTabelleBerechnetWird() {
		tabelle = new TabellenPlatz[] { //
				TabellenPlatz.builder().platz(1).team("Team 1").build(), //
				TabellenPlatz.builder().platz(2).team("Team 2").build() //
		};
	}

	private void dannIstDieTabelle(TabellenPlatz... expected) {
		assertThat(tabelle).isEqualTo(expected);
	}

}
