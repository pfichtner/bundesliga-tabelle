package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService;

@SpringBootTest
@AutoConfigureMockMvc
class TabellenHttpAdapterTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TabellenService tabellenService;

	@Test
	void shouldReturnDefaultMessage() throws Exception {
		String league = "bl1";
		String season = "2022";

		TabellenPlatz platz1 = TabellenPlatz.builder().team("Team 10").spiele(11)
				.ergebnisse(Map.of(SIEG, 12, UNENTSCHIEDEN, 13, NIEDERLAGE, 14)).toreHeim(15).toreAuswaerts(16)
				.gegentoreHeim(17).gegentoreAuswaerts(18).punkte(19).build();
		TabellenPlatz platz2 = TabellenPlatz.builder().team("Team 20").spiele(21)
				.ergebnisse(Map.of(SIEG, 22, UNENTSCHIEDEN, 23, NIEDERLAGE, 24)).toreHeim(25).toreAuswaerts(26)
				.gegentoreHeim(27).gegentoreAuswaerts(28).punkte(29).build();
		when(tabellenService.erstelleTabelle(league, season)).thenReturn(List.of(platz1, platz2));

		this.mockMvc.perform(get("/tabelle/" + league + "/" + season)) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.[0].team", is(platz1.getTeam()))) //
				.andExpect(jsonPath("$.[0].spiele", is(platz1.getSpiele()))) //
				.andExpect(jsonPath("$.[0].gewonnen", is(platz1.getGewonnen()))) //
				.andExpect(jsonPath("$.[0].unentschieden", is(platz1.getUnentschieden()))) //
				.andExpect(jsonPath("$.[0].verloren", is(platz1.getVerloren()))) //
				.andExpect(jsonPath("$.[0].tore", is(platz1.getTore()))) //
				.andExpect(jsonPath("$.[0].gegentore", is(platz1.getGegentore()))) //
				.andExpect(jsonPath("$.[0].torDifferenz", is(platz1.getTorDifferenz()))) //
				.andExpect(jsonPath("$.[0].punkte", is(platz1.getPunkte()))) //
				//
				.andExpect(jsonPath("$.[1].team", is(platz2.getTeam()))) //
				.andExpect(jsonPath("$.[1].spiele", is(platz2.getSpiele()))) //
				.andExpect(jsonPath("$.[1].gewonnen", is(platz2.getGewonnen()))) //
				.andExpect(jsonPath("$.[1].unentschieden", is(platz2.getUnentschieden()))) //
				.andExpect(jsonPath("$.[1].verloren", is(platz2.getVerloren()))) //
				.andExpect(jsonPath("$.[1].tore", is(platz2.getTore()))) //
				.andExpect(jsonPath("$.[1].gegentore", is(platz2.getGegentore()))) //
				.andExpect(jsonPath("$.[1].torDifferenz", is(platz2.getTorDifferenz()))) //
				.andExpect(jsonPath("$.[1].punkte", is(platz2.getPunkte()))) //
		;

	}

}
