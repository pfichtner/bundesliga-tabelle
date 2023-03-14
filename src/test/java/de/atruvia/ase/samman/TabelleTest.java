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

	private String[] teams;
	private TabellenPlatz[] tabelle;

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegebenSeien("Team 1", "Team 2");
		gegenSeienErgebisse();
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(tabellenplatz().platz(1).team("Team 1"),
				tabellenplatz().platz(2).team("Team 2"));
	}

	private de.atruvia.ase.samman.TabelleTest.TabellenPlatz.TabellenPlatzBuilder tabellenplatz() {
		return TabellenPlatz.builder();
	}

	private void gegebenSeien(String... teams) {
		this.teams = teams;
	}

	private void gegenSeienErgebisse() {
		// TODO Auto-generated method stub
	}

	private void wennDieTabelleBerechnetWird() {
		tabelle = new TabellenPlatz[] { //
				tabellenplatz().platz(1).team(teams[0]).build(), //
				tabellenplatz().platz(2).team(teams[1]).build() //
		};
	}

	private void dannIstDieTabelle(TabellenPlatz.TabellenPlatzBuilder... expected) {
		assertThat(tabelle).isEqualTo(Arrays.stream(expected).map(t->t.build()).toArray(TabellenPlatz[]::new));
	}

}
