package de.atruvia.ase.samman;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.IntPredicate;

import org.junit.jupiter.api.Test;

class TabelleTest {

	private String platz1;
	private String platz2;

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegebenSeien("Team1", "Team2");

		assertThat(platz1).isEqualTo("Team1");
		assertThat(platz2).isEqualTo("Team2");
	}

	private void gegebenSeien(String team1, String team2) {
		// TODO Auto-generated method stub
		
	}

}
