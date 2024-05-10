package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

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
					.letzte5(convertTendenz(domain.tendenz())) //
					.laufendesSpiel(convertLaufendesSpiel(domain.laufendesSpiel())) //
					.build();
			assert jsonTabellenPlatz.letzte5.matches(patternLetzte5)
					: jsonTabellenPlatz.letzte5 + " entspricht nicht pattern " + patternLetzte5;
			return jsonTabellenPlatz;
		}

		private static String convertTendenz(Ergebnis[] tendenz) {
			return stream(tendenz).map(JsonTabellenPlatz::convertErgebnis).map(String::valueOf).collect(joining());
		}

		private static char convertErgebnis(Ergebnis ergebnis) {
			// needs at least Java 18 (JEP-420) to have a case null
			if (ergebnis == null) {
				return '-';
			}
			return switch (ergebnis) {
			case SIEG -> 'S';
			case UNENTSCHIEDEN -> 'U';
			case NIEDERLAGE -> 'N';
			};
		}

		private static JsonLaufendesSpiel convertLaufendesSpiel(PaarungView laufendesSpiel) {
			return laufendesSpiel == null //
					? null //
					: new JsonLaufendesSpiel(convertErgebnis(laufendesSpiel.ergebnis()), laufendesSpiel.gegner().team(),
							laufendesSpiel.tore(), laufendesSpiel.gegentore());
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
