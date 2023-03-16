package de.atruvia.ase.samman;

import static de.atruvia.ase.samman.TabelleTest.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.TabelleTest.Ergebnis.SIEG;
import static de.atruvia.ase.samman.TabelleTest.Ergebnis.UNENTSCHIEDEN;

import de.atruvia.ase.samman.TabelleTest.Ergebnis;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;

@Value
@Builder(toBuilder = true) class Paarung {
	boolean gespielt;
	String team1, team2;
	int tore;
	int gegentore;

	int punkte() {
		return switch (ergebnis()) {
		case SIEG -> 3;
		case UNENTSCHIEDEN -> 1;
		case NIEDERLAGE -> 0;
		};
	}

	public Ergebnis ergebnis() {
		return tore == gegentore ? UNENTSCHIEDEN : tore > gegentore ? SIEG : NIEDERLAGE;
	}

	Paarung swap() {
		return toBuilder().team1(team2).team2(team1).tore(gegentore).gegentore(tore).build();
	}

	static class PaarungBuilder {

		Paarung.PaarungBuilder ergebnis(int tore, int gegentore) {
			this.gespielt = true;
			this.tore = tore;
			this.gegentore = gegentore;
			return this;
		}

		private Paarung.PaarungBuilder gespielt(boolean gespielt) {
			this.gespielt = gespielt;
			return this;
		}

	}

}