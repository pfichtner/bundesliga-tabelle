package de.atruvia.ase.samman.buli.domain;

import static java.util.Arrays.stream;

import java.util.List;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;

public final class TabellenPlatzMother {

	private TabellenPlatzMother() {
		super();
	}

	/**
	 * Erzeugt die Liste der Ergebnisse, wie sie {@link Tabelle} auch erzeugt. (TODO
	 * sollten wir das mit einem Spy sicherstellen?)
	 * 
	 * @param ergebnisse Ergebnistypen die über die merge Funktion zu einer Liste
	 *                   zusammengefasst werden sollen
	 * @return Liste der Ergebnisse des gemergten Tabellenplatzes
	 */
	public static List<Ergebnis> ergebnisse(Ergebnis... ergebnisse) {
		// wir könnten hier direkt die Ergebnisse ablegen, allerdings bestünde damit die
		// Gefahr, dass wir Tabellenplätze anders erzeugen, als es die echte Tabelle
		// tut (z.B. Tabelle könnte bei Spielen mit "SIEG", "UNENTSCHIEDEN",
		// "NIEDERLAGE" Ergebnisse "NIEDERLAGE", "UNENTSCHIEDEN", "SIEG" erzeugen und
		// wir würden hier "SIEG", "UNENTSCHIEDEN", "NIEDERLAGE" ablegen).
		return stream(ergebnisse).map(e -> TabellenPlatz.builder().ergebnis(e).build()).reduce(TabellenPlatz::merge)
				.orElseGet(() -> TabellenPlatz.NULL).getErgebnisse();
	}

}
