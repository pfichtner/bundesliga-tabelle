package de.atruvia.ase.samman.buli.cucumber;

import static java.lang.Integer.parseInt;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.Tabelle;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Gegebensei;
import io.cucumber.java.de.Wenn;

public class StepDefs {

	List<Paarung> paarungen = new ArrayList<>();
	Tabelle tabelle = new Tabelle();
	List<TabellenPlatz> entries;

	@Gegebensei("ein Spielplan")
	public void ein_spielplan(io.cucumber.datatable.DataTable dataTable) {
		for (Map<String, String> row : dataTable.asMaps()) {
			String[] ergebnis = row.get("Ergebnis").split(":");
			paarungen.add(PaarungBuilder.paarung(row.get("Heim"), row.get("Gast"))
					.ergebnis(parseInt(ergebnis[0]), parseInt(ergebnis[1])).build());
		}
	}

	@Wenn("die Tabelle berechnet wird")
	public void die_tabelle_berechnet_wird() {
		for (Paarung paarung : paarungen) {
			tabelle.add(paarung);
		}
		entries = tabelle.getEntries();
	}

	@Dann("ist die Tabelle")
	public void ist_die_tabelle(io.cucumber.datatable.DataTable dataTable) {
		Iterator<TabellenPlatz> iterator = entries.iterator();
		for (Map<String, String> row : dataTable.asMaps()) {
			TabellenPlatz platz = iterator.next();
			assertSoftly(s -> {
				s.assertThat(platz.getTeam()).isEqualTo(row.get("Team"));
				s.assertThat(platz.getPlatz()).isEqualTo(parseInt(row.get("Platz")));
				s.assertThat(platz.getSpiele()).isEqualTo(parseInt(row.get("Spiele")));
				s.assertThat(platz.getAnzahlSiege()).isEqualTo(parseInt(row.get("Siege")));
				s.assertThat(platz.getAnzahlUnentschieden()).isEqualTo(parseInt(row.get("Unentschieden")));
				s.assertThat(platz.getAnzahlNiederlagen()).isEqualTo(parseInt(row.get("Niederlagen")));
				s.assertThat(platz.getPunkte()).isEqualTo(parseInt(row.get("Punkte")));
				s.assertThat(platz.getTore()).isEqualTo(parseInt(row.get("Tore")));
				s.assertThat(platz.getGegentore()).isEqualTo(parseInt(row.get("Gegentore")));
				s.assertThat(platz.getTorDifferenz()).isEqualTo(parseInt(row.get("Tordifferenz")));
			});
		}
	}

}
