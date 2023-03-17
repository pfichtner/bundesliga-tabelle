package de.atruvia.ase.sammanbuli.infra.adapters.primary;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import de.atruvia.ase.sammanbuli.domain.TabellenPlatz;
import de.atruvia.ase.sammanbuli.domain.ports.primary.TabellenService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TabellenHttpAdapter {
	
	private final TabellenService tabellenService;

	@GetMapping("/tabelle/{league}/{season}")
	public List<TabellenPlatz> getTabelle(@PathVariable String league, @PathVariable String season) {
		return tabellenService.erstelleTabelle(league, season);
	}

}