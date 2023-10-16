package de.atruvia.ase.samman.buli.domain;

import static java.util.Arrays.asList;

import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;

public final class TabellenPlatzMother {

	private TabellenPlatzMother() {
		super();
	}

	/**
	 * Erzeugt die Liste der Ergebnisse, wie sie {@link TabellenRechner} auch
	 * erzeugt. (TODO sollten wir das mit einem Spy sicherstellen?)
	 * 
	 * @param ergebnisse Ergebnistypen die über die merge Funktion zu einer Liste
	 *                   zusammengefasst werden sollen
	 * @return Liste der Ergebnisse des gemergten Tabellenplatzes
	 */
	public static TabellenPlatz platzWith(Ergebnis... ergebnisse) {
		return platzWith(asList(ergebnisse));
	}

	/**
	 * Erzeugt die Liste der Ergebnisse, wie sie {@link TabellenRechner} auch
	 * erzeugt. (TODO sollten wir das mit einem Spy sicherstellen?)
	 * 
	 * @param ergebnisse Ergebnistypen die über die merge Funktion zu einer Liste
	 *                   zusammengefasst werden sollen
	 * @return Liste der Ergebnisse des gemergten Tabellenplatzes
	 */
	public static TabellenPlatz platzWith(List<Ergebnis> ergebnisse) {
		// wir könnten hier direkt die Ergebnisse ablegen, allerdings bestünde damit die
		// Gefahr, dass wir Tabellenplätze anders erzeugen, als es die echte Tabelle
		// tut (z.B. Tabelle könnte bei Spielen mit "SIEG", "UNENTSCHIEDEN",
		// "NIEDERLAGE" Ergebnisse "NIEDERLAGE", "UNENTSCHIEDEN", "SIEG" erzeugen und
		// wir würden hier "SIEG", "UNENTSCHIEDEN", "NIEDERLAGE" ablegen).
		// Wir verlassen uns allerdings hier auch darauf, dass die echte Tabelle
		// TabellenPlatz::merge nutzt.
		return ergebnisse.stream().map(TabellenPlatzMother::platzWith).reduce(TabellenPlatz::merge)
				.orElseGet(() -> TabellenPlatz.NULL);
	}

	private static TabellenPlatz platzWith(Ergebnis ergebnis) {
		return TabellenPlatz.builder().ergebnis(ergebnis).build();
	}

}
