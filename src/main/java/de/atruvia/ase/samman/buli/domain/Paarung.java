package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.GEPLANT;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.LAUFEND;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.AUSWAERTS;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.HEIM;
import static lombok.AccessLevel.PRIVATE;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Accessors(fluent = true)
public class Paarung {

	public enum ViewDirection {
		HEIM, AUSWAERTS
	}

	private final class SwappedPaarung extends Paarung {

		public SwappedPaarung(Paarung paarung) {
			super(ergebnisTyp, team2, team1);
		}

		public ViewDirection viewDirection() {
			return AUSWAERTS;
		}

		@Override
		public Paarung withSwappedTeams() {
			return Paarung.this;
		}

	}

	public enum Ergebnis {
		SIEG, UNENTSCHIEDEN, NIEDERLAGE;

		public int punkte() {
			return switch (this) {
			case SIEG -> 3;
			case UNENTSCHIEDEN -> 1;
			case NIEDERLAGE -> 0;
			};
		}
	}

	public enum ErgebnisTyp {
		GEPLANT, LAUFEND, BEENDET
	}

	@Value
	@Builder(toBuilder = true)
	public static class Entry {
		Object identifier;
		String team;
		URI wappen;
		@With
		int tore;

		public Entry(String team, URI wappen, int tore) {
			this(null, team, wappen, tore);
		}

		public Entry(Object identifier, String team, URI wappen, int tore) {
			this.identifier = identifier == null ? team : identifier;
			this.team = team;
			this.wappen = wappen;
			this.tore = tore;
		}

	}

	@Builder.Default
	ErgebnisTyp ergebnisTyp = GEPLANT;

	Entry team1, team2;

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
		int toreHeim = team1.tore();
		int toreGast = team2.tore();
		if (toreHeim == toreGast) {
			return UNENTSCHIEDEN;
		}
		return toreHeim > toreGast ? SIEG : NIEDERLAGE;
	}

	public ViewDirection viewDirection() {
		return HEIM;
	}

	public Paarung withSwappedTeams() {
		return new SwappedPaarung(this);
	}

	public static class PaarungBuilder {

		public static PaarungBuilder paarung(String team1, String team2) {
			return Paarung.builder().team1(entry(team1)).team2(entry(team2));
		}

		private static Entry entry(String team) {
			return Entry.builder().team(team).build();
		}

		public PaarungBuilder endergebnis(int toreTeam1, int toreTeam2) {
			return ergebnis(BEENDET, toreTeam1, toreTeam2);
		}

		public PaarungBuilder zwischenergebnis(int toreTeam1, int toreTeam2) {
			return ergebnis(LAUFEND, toreTeam1, toreTeam2);
		}

		private PaarungBuilder ergebnis(ErgebnisTyp ergebnisTyp, int toreTeam1, int toreTeam2) {
			return ergebnisTyp(ergebnisTyp).goals(toreTeam1, toreTeam2);
		}

		public PaarungBuilder goals(int toreTeam1, int toreTeam2) {
			return team1(team1.withTore(toreTeam1)).team2(team2.withTore(toreTeam2));
		}

	}

}