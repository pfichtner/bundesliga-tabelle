package de.atruvia.ase.samman.buli.cucumber;

import static java.lang.Integer.parseInt;
import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.Tabelle;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import io.cucumber.datatable.DataTable;
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
	@Gegebensei("der Spielplan")
	public void der_spielplan(DataTable dataTable) {
		for (var row : dataTable.asMaps()) {
			var ergebnis = row.get("Ergebnis").split(":");
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
	public void ist_die_tabelle(DataTable dataTable) {
		var iterator = entries.iterator();
		assertSoftly(s -> {
			for (var row : dataTable.asMaps()) {
				var platz = iterator.next();
				for (var entry : row.entrySet()) {
					var name = entry.getKey();
					var value = attributeValue(platz, name);
					s.assertThat(value).describedAs("Attribute '%s' differs in row %s", name, row)
							.hasToString(entry.getValue());
				}
			}
		});
	}

	private static Object attributeValue(TabellenPlatz platz, String attributeName) {
		return accessor(attributeName).apply(platz);
	}

	private static Function<TabellenPlatz, Object> accessor(String attributeName) {
		return requireNonNull(accessors.get(attributeName), () -> "unknown attribute named '" + attributeName + "'");
	}

}
