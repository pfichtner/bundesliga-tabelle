package de.atruvia.ase.samman.buli.infra.adapters.primary;

import java.net.URI;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RestController
@RequiredArgsConstructor
public class TabellenHttpAdapter {

	@Value
	@Builder
	private static class JsonTabellenPlatz {

		int platz;
		URI wappen;
		String team;
		int spiele;
		int punkte;
		int tore, gegentore, tordifferenz;
		int siege, unentschieden, niederlagen;

		private static JsonTabellenPlatz fromDomain(TabellenPlatz domain) {
			return builder() //
					.platz(domain.getPlatz()) //
					.wappen(domain.getWappen()) //
					.team(domain.getTeam()) //
					.spiele(domain.getSpiele()) //
					.punkte(domain.getPunkte()) //
					.tore(domain.getTore()) //
					.gegentore(domain.getGegentore()) //
					.tordifferenz(domain.getTorDifferenz()) //
					.siege(domain.getSiege()) //
					.unentschieden(domain.getUnentschieden()) //
					.niederlagen(domain.getNiederlagen()) //
					.build();
		}

	}

	private final TabellenService tabellenService;

	@GetMapping("/tabelle/{league}/{season}")
	public Stream<JsonTabellenPlatz> getTabelle(@PathVariable String league, @PathVariable String season) {
		return tabellenService.erstelleTabelle(league, season).stream().map(JsonTabellenPlatz::fromDomain);
	}

}
