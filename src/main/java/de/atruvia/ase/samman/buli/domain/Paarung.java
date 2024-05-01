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

	public static interface PaarungView {

		Entry team();

		Entry gegner();

		int tore();

		int gegenTore();

		ViewDirection direction();

		default Ergebnis ergebnis() {
			int toreHeim = tore();
			int toreGast = gegenTore();
			if (toreHeim == toreGast) {
				return UNENTSCHIEDEN;
			}
			return toreHeim > toreGast ? SIEG : NIEDERLAGE;
		}

		boolean hatErgebnis();

		ErgebnisTyp ergebnisTyp();

		boolean ergebnisTypIs(ErgebnisTyp ergebnisTyp);

	}

	@Value
	private class DefaultPaarungView implements PaarungView {

		Paarung delegate;
		ViewDirection direction;
		Entry team;
		Entry gegner;

		@Override
		public int tore() {
			return team().tore();
		}

		@Override
		public int gegenTore() {
			return gegner().tore();
		}

		@Override
		public boolean hatErgebnis() {
			return delegate.hatErgebnis();
		}

		@Override
		public ErgebnisTyp ergebnisTyp() {
			return delegate.ergebnisTyp;
		}

		@Override
		public boolean ergebnisTypIs(ErgebnisTyp ergebnisTyp) {
			return delegate.ergebnisTypIs(ergebnisTyp);
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

	public PaarungView heimView() {
		return new DefaultPaarungView(this, HEIM, heim, gast);
	}

	public PaarungView auswaertsView() {
		return new DefaultPaarungView(this, AUSWAERTS, gast, heim);
	}

}