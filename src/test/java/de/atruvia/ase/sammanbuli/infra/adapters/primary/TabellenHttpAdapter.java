package de.atruvia.ase.sammanbuli.infra.adapters.primary;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TabellenHttpAdapter {

	@GetMapping("/tabelle/{league}/{season}")
	public String getTabelle(@PathVariable String league, @PathVariable String season) {
		return "Hello, World";
	}

}
