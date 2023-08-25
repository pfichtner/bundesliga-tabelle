package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;

import java.net.URI;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Paarung {

	public enum Ergebnis {
		SIEG, UNENTSCHIEDEN, NIEDERLAGE;
	}

	boolean gespielt;
	String teamHeim, teamGast;
	URI wappenHeim, wappenGast;
	int toreTeamHeim;
	int toreTeamGast;

	public int punkte() {
		return switch (ergebnis()) {
		case SIEG -> 3;
		case UNENTSCHIEDEN -> 1;
		case NIEDERLAGE -> 0;
		};
	}

	public Ergebnis ergebnis() {
		return toreTeamHeim == toreTeamGast ? UNENTSCHIEDEN : toreTeamHeim > toreTeamGast ? SIEG : NIEDERLAGE;
	}

	public Paarung swap() {
		return toBuilder().teamHeim(teamGast).teamGast(teamHeim) //
				.wappenHeim(wappenGast).wappenGast(wappenHeim) //
				.toreTeamHeim(toreTeamGast).toreTeamGast(toreTeamHeim) //
				.build();
	}

	public static class PaarungBuilder {

		public PaarungBuilder ergebnis(int toreTeamHeim, int toreTeamGast) {
			this.gespielt = true;
			this.toreTeamHeim = toreTeamHeim;
			this.toreTeamGast = toreTeamGast;
			return this;
		}

		private PaarungBuilder gespielt(boolean gespielt) {
			this.gespielt = gespielt;
			return this;
		}

	}

}