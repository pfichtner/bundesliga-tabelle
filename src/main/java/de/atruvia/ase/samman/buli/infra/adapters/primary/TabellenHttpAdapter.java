package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static java.lang.Math.min;
import static java.util.Arrays.fill;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungView;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RestController
@RequiredArgsConstructor
public class TabellenHttpAdapter {

	@Value
	@Builder
	private static class JsonLaufendesSpiel {
		@Schema(description = "Mögliche Ausprägungen: 'S' (Sieg), 'U' (Unentschieden), 'N' (Niederlage). "
				+ "Da das Spiel noch nicht beendet ist handelt es sich eigentlich nicht um Sieg bzw. Niederlage sondern um Führung bzw. Rückstand. ", allowableValues = {
						"S", "U", "N" })
		char ergebnis;
		@Schema(description = "Teamname des gegnerischen Teams")
		String gegner;
		@Schema(description = "Geschossene Tore des Teams")
		int tore;
		@Schema(description = "Geschossene Tore des gegnerischen Teams")
		int toreGegner;
	}

	@Value
	@Builder
	@JsonInclude(NON_NULL)
	private static class JsonTabellenPlatz {
		private static final String patternLetzte5 = "[SUN-]{5}";

		int platz;
		@Schema(description = "URI des Vereinswappens/-logos. Im Normallfall gesetzt, kann aber potentiell null sein. ", nullable = true)
		String wappen;
		String team;
		int spiele;
		int punkte;
		int tore, gegentore, tordifferenz;
		int siege, unentschieden, niederlagen;
		@Schema(description = "Ergebnisse der letzten fünf Spiele. "
				+ "Enthält 5 Zeichen, jeweils 'S' (Sieg), 'U' (Unentschieden), 'N' (Niederlage) oder '-' (nicht gespielt). Nur beendete (nicht laufende) Spiele werden berücksichtigt. ", pattern = patternLetzte5)
		String letzte5;
		@Schema(description = "Information zum Spiel, falls dieses Team derzeit gegen einen andere Mannschaft in dieser Liga spielt, ansonsten nicht gesetzt. ", nullable = true)
		JsonLaufendesSpiel laufendesSpiel;

		private static JsonTabellenPlatz fromDomain(TabellenPlatz domain) {
			JsonTabellenPlatz jsonTabellenPlatz = builder() //
					.platz(domain.platz()) //
					.wappen(domain.wappen() == null ? null : domain.wappen().toASCIIString()) //
					.team(domain.team()) //
					.spiele(domain.spiele()) //
					.punkte(domain.punkte()) //
					.tore(domain.gesamtTore()) //
					.gegentore(domain.gesamtGegentore()) //
					.tordifferenz(domain.torDifferenz()) //
					.siege(domain.siege()) //
					.unentschieden(domain.unentschieden()) //
					.niederlagen(domain.niederlagen()) //
					.letzte5(concatToSingleString(domain.ergebnisse(BEENDET), 5, '-')) //
					.laufendesSpiel(
							domain.laufendesSpiel() == null ? null : convertLaufendesSpiel(domain.laufendesSpiel())) //
					.build();
			assert jsonTabellenPlatz.letzte5.matches(patternLetzte5)
					: jsonTabellenPlatz.letzte5 + " entspricht nicht pattern " + patternLetzte5;
			return jsonTabellenPlatz;
		}

		private static JsonLaufendesSpiel convertLaufendesSpiel(PaarungView view) {
			return new JsonLaufendesSpiel(convertErgebnis(view.ergebnis()), view.gegner().team(), view.tore(),
					view.gegenTore());
		}

		/**
		 * Liefert die letzten <code>length</code> Ergebnisse. Das jüngste Ergebnis ist
		 * vorne, das älteste Ergebnis hinten im String.
		 * 
		 * @param ergebnisse zu übersetzende Ergebnisse
		 * @param length     Länge des zu erzeugenden Strings. Sind weniger Ergebnisse
		 *                   vorhanden als der String lang sein soll wird dieser mit
		 *                   <code>filler</code> aufgefüllt
		 * @param filler     Zeichen mit dem der String aufgefüllt werden soll, falls
		 *                   nicht ausreichend Ergebnisse vorhanden sind
		 * @return String der letzen <code>length</code> Ergebnisse bestehend aus 'S',
		 *         'U', 'N'
		 */
		private static String concatToSingleString(List<Ergebnis> ergebnisse, int length, char filler) {
			char[] chars = new char[length];
			int idx = 0;
			for (; idx < min(ergebnisse.size(), chars.length); idx++) {
				chars[idx] = convertErgebnis(ergebnisse.get(ergebnisse.size() - 1 - idx));
			}
			fill(chars, idx, chars.length, filler);
			return new String(chars);
		}

		private static char convertErgebnis(Ergebnis ergebnis) {
			return switch (ergebnis) {
			case SIEG -> 'S';
			case UNENTSCHIEDEN -> 'U';
			case NIEDERLAGE -> 'N';
			};
		}

	}

	private final TabellenService tabellenService;

	// Spring supports Stream<JsonTabellenPlatz> but then swagger cannot detect the
	// returntype so collect it into a List that gets returned
	@GetMapping("/tabelle/{league}/{season}")
	public List<JsonTabellenPlatz> getTabelle(@PathVariable String league, @PathVariable String season) {
		return tabellenService.erstelleTabelle(league, season).stream().map(JsonTabellenPlatz::fromDomain).toList();
	}

}
