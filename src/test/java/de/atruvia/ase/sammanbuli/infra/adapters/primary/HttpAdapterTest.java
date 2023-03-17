package de.atruvia.ase.sammanbuli.infra.adapters.primary;

import static de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.sammanbuli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import de.atruvia.ase.samman.buli.Main;
import de.atruvia.ase.sammanbuli.domain.TabellenPlatz;
import de.atruvia.ase.sammanbuli.domain.ports.primary.DefaultTabellenService;
import de.atruvia.ase.sammanbuli.domain.ports.primary.TabellenService;
import de.atruvia.ase.sammanbuli.infra.adapters.secondary.OpenLigaDbSpieltagRepo;

@SpringBootTest(classes = { Main.class, TabellenHttpAdapter.class, DefaultTabellenService.class,
		OpenLigaDbSpieltagRepo.class })
@AutoConfigureMockMvc
class HttpAdapterTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TabellenService tabellenService;

	@Test
	void shouldReturnDefaultMessage() throws Exception {
		TabellenPlatz platz1 = TabellenPlatz.builder().team("Team 10").spiele(11)
				.ergebnisse(Map.of(SIEG, 12, UNENTSCHIEDEN, 13, NIEDERLAGE, 14)).toreHeim(15).toreAuswaerts(16)
				.gegentoreHeim(17).gegentoreAuswaerts(18).punkte(19).build();
		TabellenPlatz platz2 = TabellenPlatz.builder().team("Team 20").spiele(21)
				.ergebnisse(Map.of(SIEG, 22, UNENTSCHIEDEN, 23, NIEDERLAGE, 24)).toreHeim(25).toreAuswaerts(26)
				.gegentoreHeim(27).gegentoreAuswaerts(28).punkte(29).build();
		when(tabellenService.erstelleTabelle("bl1", "2022")).thenReturn(List.of(platz1, platz2));

		this.mockMvc.perform(get("/tabelle/bl1/2022")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.[0].team", is(platz1.getTeam()))) //
				.andExpect(jsonPath("$.[0].spiele", is(platz1.getSpiele()))) //
				.andExpect(jsonPath("$.[0].gewonnen", is(platz1.getGewonnen()))) //
				.andExpect(jsonPath("$.[0].unentschieden", is(platz1.getUnentschieden()))) //
				.andExpect(jsonPath("$.[0].verloren", is(platz1.getVerloren()))) //
				.andExpect(jsonPath("$.[0].tore", is(platz1.getTore()))) //
				.andExpect(jsonPath("$.[0].gegentore", is(platz1.getGegentore()))) //
				.andExpect(jsonPath("$.[0].tordifferenz", is(platz1.getTorDifferenz()))) //
				.andExpect(jsonPath("$.[0].punkte", is(platz1.getPunkte()))) //
		;

	}

}
