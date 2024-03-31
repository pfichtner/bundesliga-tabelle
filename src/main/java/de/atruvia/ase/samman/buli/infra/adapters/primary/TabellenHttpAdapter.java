package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static de.atruvia.ase.samman.buli.domain.Paarung.ErgebnisTyp.BEENDET;
import static java.lang.Math.max;
import static java.util.Collections.reverse;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
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
					.letzte5(convertLetzte5(domain)) //
					.build();
		}

		private static String convertLetzte5(TabellenPlatz platz) {
			return String.format("%-5s", lastNErgebnisse(platz, 5).stream().map(e -> switch (e) {
			case SIEG -> "S";
			case UNENTSCHIEDEN -> "U";
			case NIEDERLAGE -> "N";
			}).collect(joining())).replace(' ', '-');
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

	@GetMapping("/tabelle/{league}/{season}")
	public Stream<JsonTabellenPlatz> getTabelle(@PathVariable String league, @PathVariable String season) {
		return tabellenService.erstelleTabelle(league, season).stream().map(JsonTabellenPlatz::fromDomain);
	}

}
