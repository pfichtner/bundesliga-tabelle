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

	public Ergebnis ergebnis() {
		return toreTeamHeim == toreTeamGast //
				? UNENTSCHIEDEN //
				: toreTeamHeim > toreTeamGast //
						? SIEG //
						: NIEDERLAGE;
	}

	public Paarung swap() {
		return toBuilder() //
				.teamHeim(getTeamGast()).teamGast(getTeamHeim()) //
				.wappenHeim(getWappenGast()).wappenGast(getWappenHeim()) //
				.toreTeamHeim(getToreTeamGast()).toreTeamGast(getToreTeamHeim()) //
				.build();
	}

	public static class PaarungBuilder {

		public static Paarung.PaarungBuilder paarung(String teamHeim, String teamGast) {
			return Paarung.builder().teamHeim(teamHeim).teamGast(teamGast);
		}

		public PaarungBuilder ergebnis(int toreTeamHeim, int toreTeamGast) {
			return toreTeamHeim(toreTeamHeim).toreTeamGast(toreTeamGast).gespielt(true);
		}

	}

}