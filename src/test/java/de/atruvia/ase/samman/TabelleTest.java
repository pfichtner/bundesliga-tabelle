package de.atruvia.ase.samman;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TabelleTest {

	private static class TabellenPlatz {

	}

	private String[] teams;
	private String[] plaetze;

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegebenSeien("Team1", "Team2");
		gegenSeienErgebisse();
		wennDieTabelleBerechnetWird();
		dannSindDiePlatzierungen("Team1", "Team2");
		dannIstDieTabelle(new TabellenPlatz(), new TabellenPlatz());
	}

	private void gegebenSeien(String... teams) {
		this.teams = teams;
	}

	private void gegenSeienErgebisse() {
		// TODO Auto-generated method stub
	}

	private void wennDieTabelleBerechnetWird() {
		plaetze = teams;
	}

	private void dannSindDiePlatzierungen(String... expected) {
		assertThat(plaetze).isEqualTo(expected);
	}

	private void dannIstDieTabelle(TabellenPlatz tabellenPlatz, TabellenPlatz tabellenPlatz2) {
		// TODO Auto-generated method stub

	}

}
