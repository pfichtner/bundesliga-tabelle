package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.GEPLANT;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.LAUFEND;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class Paarung {

	public enum Ergebnis {
		SIEG, UNENTSCHIEDEN, NIEDERLAGE;
	}

	public enum ErgebnisTyp {
		GEPLANT, LAUFEND, BEENDET;
	}

	@Data
	@AllArgsConstructor
	@Accessors(fluent = true)
	public static class Entry {
		String team;
		URI wappen;
		int tore;

		public Entry(String team, URI wappen) {
			this(team, wappen, 0);
		}
	}

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

	public Paarung endergebnis(int toreHeim, int toreGast) {
		return ergebnis(BEENDET, toreHeim, toreGast);
	}

	public Paarung zwischenergebnis(int toreHeim, int toreGast) {
		return ergebnis(LAUFEND, toreHeim, toreGast);
	}

	public Paarung ergebnis(ErgebnisTyp ergebnisTyp, int toreHeim, int toreGast) {
		return new Paarung(ergebnisTyp, //
				new Entry(heim.team, heim.wappen, toreHeim), //
				new Entry(gast.team, gast.wappen, toreGast) //
		);
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
		return new Paarung(ergebnisTyp, gast, heim);
	}

}