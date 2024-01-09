package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class Paarung {

	public enum Ergebnis {
		SIEG, UNENTSCHIEDEN, NIEDERLAGE;
	}

	@Value
	@AllArgsConstructor
	@Builder(toBuilder = true)
	public static class Entry {
		String team;
		URI wappen;
		@With
		int tore;

		public Entry(String team, URI wappen) {
			this(team, wappen, 0);
		}
	}

	boolean gespielt;
	Entry heim, gast;

	public Paarung(boolean gespielt, String teamHeim, String teamGast, URI wappenHeim, URI wappenGast, int toreTeamHeim,
			int toreTeamGast) {
		this.gespielt = gespielt;
		this.heim = new Entry(teamHeim, wappenHeim, toreTeamHeim);
		this.gast = new Entry(teamGast, wappenGast, toreTeamGast);
	}

	public String getTeamHeim() {
		return team(heim);
	}

	public String getTeamGast() {
		return team(gast);
	}

	private static String team(Entry entry) {
		return entry.team;
	}

	public URI getWappenHeim() {
		return wappen(heim);
	}

	public URI getWappenGast() {
		return wappen(gast);
	}

	private static URI wappen(Entry entry) {
		return entry.wappen;
	}

	public int getToreTeamHeim() {
		return tore(heim);
	}

	public int getToreTeamGast() {
		return tore(gast);
	}

	private static int tore(Entry entry) {
		return entry.tore;
	}

	public Paarung withErgebnis(int toreHeim, int toreGast) {
		return toBuilder().ergebnis(toreHeim, toreGast).build();
	}

	public Ergebnis ergebnis() {
		int toreHeim = getToreTeamHeim();
		int toreGast = getToreTeamGast();
		return toreHeim == toreGast //
				? UNENTSCHIEDEN //
				: toreHeim > toreGast //
						? SIEG //
						: NIEDERLAGE;
	}

	public Paarung swap() {
		return toBuilder().heim(gast).gast(heim).build();
	}

	public static class PaarungBuilder {

		public static Paarung.PaarungBuilder paarung(String teamHeim, String teamGast) {
			return Paarung.builder().heim(entry(teamHeim)).gast(entry(teamGast));
		}

		private static Entry entry(String team) {
			return Entry.builder().team(team).build();
		}

		public PaarungBuilder ergebnis(int toreTeamHeim, int toreTeamGast) {
			return gespielt(true) //
					.heim(heim.withTore(toreTeamHeim)) //
					.gast(gast.withTore(toreTeamGast)) //
			;
		}

	}

}