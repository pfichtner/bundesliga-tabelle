package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RestController
@RequiredArgsConstructor
public class TabellenHttpAdapter {

	public static enum JsonErgebnis {
		N, U, S;

		public static List<JsonErgebnis> fromDomain(List<Ergebnis> ergebnisse) {
			return ergebnisse.stream().map(JsonErgebnis::fromDomain).toList();
		}

		public static JsonErgebnis fromDomain(Ergebnis ergebnis) {
			return switch (ergebnis) {
			case SIEG -> JsonErgebnis.S;
			case UNENTSCHIEDEN -> JsonErgebnis.U;
			case NIEDERLAGE -> JsonErgebnis.N;
			};
		}

	}

	@Value
	@Builder
	private static class JsonLaufendesSpiel {
		@Schema(description = "Mögliche Ausprägungen: 'S' (Sieg), 'U' (Unentschieden), 'N' (Niederlage). "
				+ "Da das Spiel noch nicht beendet ist handelt es sich eigentlich nicht um Sieg bzw. Niederlage sondern um Führung bzw. Rückstand. ", allowableValues = {
						"S", "U", "N" })
		JsonErgebnis ergebnis;
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
	public static class JsonTabellenPlatz {

		private static final String patternLetzte5 = "[SUN-]{5}";
		private static final int TENDENZ_MAX_LENGTH = 5;

		int platz;
		@Schema(description = "URI des Vereinswappens/-logos. Im Normallfall gesetzt, kann aber potentiell null sein. ", nullable = true)
		String wappen;
		String team;
		int spiele;
		int punkte;
		int tore, gegentore, tordifferenz;
		int siege, unentschieden, niederlagen;
		@Deprecated
		@Schema(deprecated = true, description = "Ergebnisse der letzten fünf Spiele. "
				+ "Enthält 5 Zeichen, jeweils 'S' (Sieg), 'U' (Unentschieden), 'N' (Niederlage) oder '-' (nicht gespielt). Nur beendete (nicht laufende) Spiele werden berücksichtigt. ", maxLength = TENDENZ_MAX_LENGTH, pattern = patternLetzte5)
		String letzte5;
		@ArraySchema(schema = @Schema(description = "Ergebnisse der letzten fünf Spiele. "
				+ "Enthält 'S' (Sieg), 'U' (Unentschieden), 'N' (Niederlage). Nur beendete (nicht laufende) Spiele werden berücksichtigt. ", pattern = "[SUN]"), maxItems = TENDENZ_MAX_LENGTH)
		List<JsonErgebnis> tendenz;
		@Schema(description = "Information zum Spiel, falls dieses Team derzeit gegen einen andere Mannschaft in dieser Liga spielt, ansonsten nicht gesetzt. ", nullable = true)
		JsonLaufendesSpiel laufendesSpiel;

		private static JsonTabellenPlatz fromDomain(TabellenPlatz domain) {
			JsonTabellenPlatz jsonTabellenPlatz = builder() //
					.platz(domain.platz()) //
					.wappen(domain.wappen() == null ? null : domain.wappen().toASCIIString()) //
					.team(domain.teamName()) //
					.spiele(domain.spiele()) //
					.punkte(domain.punkte()) //
					.tore(domain.gesamtTore()) //
					.gegentore(domain.gesamtGegentore()) //
					.tordifferenz(domain.torDifferenz()) //
					.siege(domain.siege()) //
					.unentschieden(domain.unentschieden()) //
					.niederlagen(domain.niederlagen()) //
					.letzte5(domain.tendenz().toASCIIString()) //
					.tendenz(JsonErgebnis.fromDomain(domain.tendenz().ergebnisse())) //
					.laufendesSpiel(convertLaufendesSpiel(domain)) //
					.build();
			assert jsonTabellenPlatz.letzte5.matches(patternLetzte5)
					: jsonTabellenPlatz.letzte5 + " entspricht nicht pattern " + patternLetzte5;
			assert jsonTabellenPlatz.tendenz.size() <= TENDENZ_MAX_LENGTH
					: jsonTabellenPlatz.tendenz + " länger als vereinbart ";
			return jsonTabellenPlatz;
		}

		private static JsonLaufendesSpiel convertLaufendesSpiel(TabellenPlatz domain) {
			var paarung = domain.laufendesSpiel();
			return paarung == null //
					? null //
					: new JsonLaufendesSpiel( //
							JsonErgebnis.fromDomain(paarung.ergebnis()), //
							paarung.gegner().team(), //
							paarung.tore(), //
							paarung.gegentore() //
					);
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
