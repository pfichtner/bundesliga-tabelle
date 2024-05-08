package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.GEPLANT;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.LAUFEND;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.AUSWAERTS;
import static de.atruvia.ase.samman.buli.domain.Paarung.ViewDirection.HEIM;

import java.net.URI;
import java.util.function.Function;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

@Value
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class Paarung {

	@Value
	public class PaarungView {

		ViewDirection direction;
		Function<Paarung, Entry> team;
		Function<Paarung, Entry> gegner;

		public Entry team() {
			return team.apply(paarung());
		}

		public Entry gegner() {
			return gegner.apply(paarung());
		}

		public int tore() {
			return team().tore();
		}

		public int gegenTore() {
			return gegner().tore();
		}

		public boolean hatErgebnis() {
			return paarung().hatErgebnis();
		}

		public Ergebnis ergebnis() {
			var tore = tore();
			var gegentore = gegenTore();
			if (tore == gegentore) {
				return UNENTSCHIEDEN;
			}
			return tore > gegentore ? SIEG : NIEDERLAGE;
		}

		public ErgebnisTyp ergebnisTyp() {
			return paarung().ergebnisTyp;
		}

		public boolean ergebnisTypIs(ErgebnisTyp ergebnisTyp) {
			return paarung().ergebnisTypIs(ergebnisTyp);
		}

		private Paarung paarung() {
			return Paarung.this;
		}

	}

	public enum ViewDirection {
		HEIM, AUSWAERTS
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

		public Entry(Object identifier, String team, URI wappen, int tore) {
			this.identifier = identifier == null ? team : identifier;
			this.team = team;
			this.wappen = wappen;
			this.tore = tore;
		}

	}

	@Builder.Default
	ErgebnisTyp ergebnisTyp = GEPLANT;

	Entry heim, gast;

	public boolean hatErgebnis() {
		return !ergebnisTypIs(GEPLANT);
	}

	public boolean ergebnisTypIs(ErgebnisTyp ergebnisTyp) {
		return this.ergebnisTyp == ergebnisTyp;
	}

	public Paarung withErgebnis(int toreHeim, int toreGast) {
		return toBuilder().endergebnis(toreHeim, toreGast).build();
	}

	public ViewDirection viewDirection() {
		return HEIM;
	}

	public static class PaarungBuilder {

		public static PaarungBuilder paarung(String heim, String gast) {
			return Paarung.builder().heim(entry(heim)).gast(entry(gast));
		}

		private static Entry entry(String team) {
			return Entry.builder().team(team).build();
		}

		public PaarungBuilder endergebnis(int toreHeim, int toreGast) {
			return ergebnis(BEENDET, toreHeim, toreGast);
		}

		public PaarungBuilder zwischenergebnis(int toreHeim, int toreGast) {
			return ergebnis(LAUFEND, toreHeim, toreGast);
		}

		private PaarungBuilder ergebnis(ErgebnisTyp ergebnisTyp, int toreHeim, int toreGast) {
			return ergebnisTyp(ergebnisTyp).goals(toreHeim, toreGast);
		}

		public PaarungBuilder goals(int toreHeim, int toreGast) {
			return heim(heim.withTore(toreHeim)).gast(gast.withTore(toreGast));
		}

	}

	public PaarungView viewForTeam(ViewDirection viewDirection) {
		return switch (viewDirection) {
		case HEIM -> new PaarungView(HEIM, Paarung::heim, Paarung::gast);
		case AUSWAERTS -> new PaarungView(AUSWAERTS, Paarung::gast, Paarung::heim);
		};
	}

}