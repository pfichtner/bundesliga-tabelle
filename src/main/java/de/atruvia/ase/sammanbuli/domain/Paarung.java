package de.atruvia.ase.sammanbuli.domain;

import static de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.sammanbuli.domain.TabelleTest.Ergebnis.*;
import static de.atruvia.ase.sammanbuli.domain.TabelleTest.Ergebnis.NIEDERLAGE;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Paarung {

	public enum Ergebnis {
		SIEG, UNENTSCHIEDEN, NIEDERLAGE;
	}

	boolean gespielt;
	String team1, team2;
	int tore;
	int gegentore;

	public int punkte() {
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

	public static class PaarungBuilder {

		public Paarung.PaarungBuilder ergebnis(int tore, int gegentore) {
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