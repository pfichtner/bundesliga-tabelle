package de.atruvia.ase.samman.buli.cucumber;

import static java.lang.Integer.parseInt;
import static java.util.Map.entry;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.Tabelle;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import io.cucumber.java.de.Dann;
import io.cucumber.java.de.Gegebensei;
import io.cucumber.java.de.Wenn;

public class StepDefs {

	private static final Map<String, Function<TabellenPlatz, Object>> accessors = Map.ofEntries( //
			entry("Team", TabellenPlatz::getTeam), //
			entry("Platz", TabellenPlatz::getPlatz), //
			entry("Spiele", TabellenPlatz::getSpiele), //
			entry("Siege", TabellenPlatz::getAnzahlSiege), //
			entry("Unentschieden", TabellenPlatz::getAnzahlUnentschieden), //
			entry("Niederlagen", TabellenPlatz::getAnzahlNiederlagen), //
			entry("Punkte", TabellenPlatz::getPunkte), //
			entry("Tore", TabellenPlatz::getTore), //
			entry("Gegentore", TabellenPlatz::getGegentore), //
			entry("Tordifferenz", TabellenPlatz::getTorDifferenz) //
	);

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

		assertSoftly(s -> {
			for (Map<String, String> row : dataTable.asMaps()) {
				TabellenPlatz platz = iterator.next();
				for (Entry<String, String> entry : row.entrySet()) {
					var attributeName = entry.getKey();
					var accessor = accessors.get(attributeName);
					var actual = accessor.apply(platz);

					s.assertThat(String.valueOf(actual)).describedAs("Attribute '%s' differs in %s", attributeName, row)
							.isEqualTo(String.valueOf(entry.getValue()));
				}
			}
		});

	}

}
