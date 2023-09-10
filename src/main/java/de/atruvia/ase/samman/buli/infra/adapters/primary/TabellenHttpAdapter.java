package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService.Tabelle;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RestController
@RequiredArgsConstructor
public class TabellenHttpAdapter {

	@Value
	private static class JsonSeason {
		String name;
	}

	@Value
	private static class JsonResult {
		JsonSeason saison;
		List<JsonTabellenPlatz> eintrag;
	}

	@Value
	@Builder
	private static class JsonTabellenPlatz {
		int platz;
		String wappen;
		String team;
		int spiele;
		int punkte;
		int tore, gegentore, tordifferenz;
		int siege, unentschieden, niederlagen;
		String letzte5;

		private static JsonTabellenPlatz fromDomain(TabellenPlatz domain) {
			return builder() //
					.platz(domain.getPlatz()) //
					.wappen(domain.getWappen() == null ? null : domain.getWappen().toASCIIString()) //
					.team(domain.getTeam()) //
					.spiele(domain.getSpiele()) //
					.punkte(domain.getPunkte()) //
					.tore(domain.getTore()) //
					.gegentore(domain.getGegentore()) //
					.tordifferenz(domain.getTorDifferenz()) //
					.siege(domain.getAnzahlSiege()) //
					.unentschieden(domain.getAnzahlUnentschieden()) //
					.niederlagen(domain.getAnzahlNiederlagen()) //
					.letzte5(convert(domain.getLetzte(5))) //
					.build();
		}

		private static String convert(List<Ergebnis> ergebnisse) {
			return format("%-5s", ergebnisse.stream().map(e -> e.name().substring(0, 1)).collect(joining()))
					.replace(' ', '-');
		}

	}

	private final TabellenService tabellenService;

	@GetMapping("/tabelle/{league}/{season}")
	public JsonResult getTabelle(@PathVariable String league, @PathVariable String season) {
		Tabelle tabelle = tabellenService.erstelleTabelle(league, season);
		JsonSeason saison = new JsonSeason(tabelle.name());
		List<JsonTabellenPlatz> plaetze = tabelle.eintraege().stream().map(JsonTabellenPlatz::fromDomain).toList();
		return new JsonResult(saison, plaetze);
	}

}
