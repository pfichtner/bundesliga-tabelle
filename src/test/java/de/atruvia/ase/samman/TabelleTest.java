package de.atruvia.ase.samman;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;

class TabelleTest {

	private String[] teams;
	private String[] plaetze;

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegebenSeien("Team1", "Team2");
		gegenSeienErgebisse();
		wennDieTabelleBerechnetWird();
		dannSindDiePlatzierungen("Team1", "Team2");
	}

	private void gegebenSeien(String... teams) {
		this.teams = teams;
	}

	private void wennDieTabelleBerechnetWird() {
//		plaetze = teams;
	}

	private void dannSindDiePlatzierungen(String... expected) {
		assertThat(plaetze).isEqualTo(expected);
	}

	private void gegenSeienErgebisse() {
		// TODO Auto-generated method stub
	}

}
