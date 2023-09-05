package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.PaarungMother.paarungen;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz.TabellenPlatzBuilder;

public final class TabellenPlatzMother {

	private TabellenPlatzMother() {
		super();
	}

	public static TabellenPlatzBuilder erzeugeErgebnisse(TabellenPlatzBuilder builder, Ergebnis... ergebnisse) {
		// wir könnten hier direkt die Ergebnisse "SIEG, UNENTSCHIEDEN, NIEDERLAGE"
		// ablegen, allerdings bestünde damit die Gefahr, dass wir Tabellenplätze anders
		// erzeugen, als es die echte Tabelle tut.
		return builder.ergebnisse(tabellenPlatzCalculatedByTabelle(ergebnisse).getErgebnisse());
	}

	private static TabellenPlatz tabellenPlatzCalculatedByTabelle(Ergebnis... ergebnisse) {
		String firstTeam = "anyTeamName";
		Tabelle tabelle = new Tabelle();
		paarungen(firstTeam, ergebnisse).stream().forEach(tabelle::add);
		return tabelle.getEntries().stream().filter(e -> e.getTeam().equals(firstTeam)).findFirst().get();
	}

}
