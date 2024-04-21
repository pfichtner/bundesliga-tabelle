package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.GEPLANT;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.LAUFEND;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class Paarung {

	public enum Ergebnis {
		SIEG, UNENTSCHIEDEN, NIEDERLAGE;
	}

	public enum ErgebnisTyp {
		GEPLANT, LAUFEND, BEENDET;
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

	@Builder.Default
	ErgebnisTyp ergebnisTyp = GEPLANT;

	Entry heim, gast;

	public String teamHeim() {
		return team(heim);
	}

	public String teamGast() {
		return team(gast);
	}

	private static String team(Entry entry) {
		return entry.team;
	}

	public URI wappenHeim() {
		return wappen(heim);
	}

	public URI wappenGast() {
		return wappen(gast);
	}

	private static URI wappen(Entry entry) {
		return entry.wappen;
	}

	public int toreHeim() {
		return tore(heim);
	}

	public int toreGast() {
		return tore(gast);
	}

	private static int tore(Entry entry) {
		return entry.tore;
	}

	public boolean hatErgebnis() {
		return !ergebnisTypIs(GEPLANT);
	}

	public boolean ergebnisTypIs(ErgebnisTyp ergebnisTyp) {
		return this.ergebnisTyp == ergebnisTyp;
	}

	public Paarung withErgebnis(int toreHeim, int toreGast) {
		return toBuilder().endergebnis(toreHeim, toreGast).build();
	}

	public Ergebnis ergebnis() {
		int toreHeim = toreHeim();
		int toreGast = toreGast();
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

		public PaarungBuilder endergebnis(int toreTeamHeim, int toreTeamGast) {
			return ergebnis(BEENDET, toreTeamHeim, toreTeamGast);
		}

		public PaarungBuilder zwischenergebnis(int toreTeamHeim, int toreTeamGast) {
			return ergebnis(LAUFEND, toreTeamHeim, toreTeamGast);
		}

		public PaarungBuilder ergebnis(ErgebnisTyp ergebnisTyp, int toreTeamHeim, int toreTeamGast) {
			return withGoals(ergebnisTyp(ergebnisTyp), toreTeamHeim, toreTeamGast);
		}

		private PaarungBuilder withGoals(PaarungBuilder builder, int toreTeamHeim, int toreTeamGast) {
			return builder //
					.heim(heim.withTore(toreTeamHeim)) //
					.gast(gast.withTore(toreTeamGast)) //
			;
		}

	}

}