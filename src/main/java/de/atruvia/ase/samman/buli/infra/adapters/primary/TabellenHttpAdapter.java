package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static java.lang.Math.max;
import static java.util.Collections.reverse;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
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
		String ergebnis;
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
					.tore(domain.tore()) //
					.gegentore(domain.gegentore()) //
					.tordifferenz(domain.torDifferenz()) //
					.siege(domain.siege()) //
					.unentschieden(domain.unentschieden()) //
					.niederlagen(domain.niederlagen()) //
					.letzte5(convertLetzte5(domain)) //
					.laufendesSpiel(
							domain.laufendesSpiel() == null ? null : convertLaufendesSpiel(domain.laufendesSpiel())) //
					.build();
			assert jsonTabellenPlatz.letzte5.matches(patternLetzte5)
					: jsonTabellenPlatz.letzte5 + " entspricht nicht pattern " + patternLetzte5;
			return jsonTabellenPlatz;
		}

		private static JsonLaufendesSpiel convertLaufendesSpiel(Paarung laufendesSpiel) {
			return new JsonLaufendesSpiel(convertErgebnis(laufendesSpiel.ergebnis()), laufendesSpiel.getTeamGast(),
					laufendesSpiel.getToreTeamHeim(), laufendesSpiel.getToreTeamGast());
		}

		private static String convertLetzte5(TabellenPlatz platz) {
			return String.format("%-5s",
					lastNErgebnisse(platz, 5).stream().map(JsonTabellenPlatz::convertErgebnis).collect(joining()))
					.replace(' ', '-');
		}

		private static String convertErgebnis(Ergebnis ergebnis) {
			return switch (ergebnis) {
			case SIEG -> "S";
			case UNENTSCHIEDEN -> "U";
			case NIEDERLAGE -> "N";
			};
		}

		/**
		 * Liefert die letzten n Ergebnisse. Sind weniger als n Ergebnisse vorhanden, so
		 * beinhaltet die Liste nur die vorhandenen Ergebnisse. Das jüngste Ergebnis ist
		 * vorne, das älteste Ergebnis hinten in der Liste.
		 * 
		 * @param platz der TabellenPlatz, von welchem die letzten n Ergebnisse
		 *              ermittelt werden sollen
		 * @param count (maximale) Anzahl an Ergebnissen die zurückgegeben werden sollen
		 * @return Liste der letzen n Ergebnisse
		 */
		private static List<Ergebnis> lastNErgebnisse(TabellenPlatz platz, int count) {
			List<Ergebnis> ergebnisse = platz.getErgebnisse(BEENDET);
			List<Ergebnis> lastN = new ArrayList<>(
					ergebnisse.subList(max(0, ergebnisse.size() - count), ergebnisse.size()));
			reverse(lastN);
			return lastN;
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
