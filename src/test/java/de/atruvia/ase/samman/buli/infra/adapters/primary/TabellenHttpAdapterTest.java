package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.TabellenPlatzMother.ergebnisse;
import static java.net.URI.create;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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

		// it seems weird that we mock a pojo class but we would depend on the symmetry
		// of setErgebnisse and getErgebnisse because we want the GETTER to return SIEG,
		// UNENTSCHIEDEN, NIEDERLAGE which we could not guarantee to be true when
		// setting SIEG, UNENTSCHIEDEN, NIEDERLAGE via the setter

		// TODO we could consider using an Interface instead of using Mockito

		// TODO at the moment "getLetzte" is implemented in TabellenPlatz so we need a
		// spy instead of a mock
		TabellenPlatz platz1 = spy(TabellenPlatz.builder().build());
		when(platz1.getErgebnisse()).thenReturn(ergebnisse(SIEG, UNENTSCHIEDEN, NIEDERLAGE));
		when(platz1.getTeam()).thenReturn("Team 10");
		when(platz1.getWappen()).thenReturn(create("proto://wappen-team-10"));
		when(platz1.getSpiele()).thenReturn(11);
		when(platz1.getToreHeim()).thenReturn(15);
		when(platz1.getToreAuswaerts()).thenReturn(16);
		when(platz1.getGegentoreHeim()).thenReturn(17);
		when(platz1.getGegentoreAuswaerts()).thenReturn(18);
		when(platz1.getPunkte()).thenReturn(19);

		TabellenPlatz platz2 = spy(TabellenPlatz.builder().build());
		when(platz2.getErgebnisse()).thenReturn(emptyList());
		when(platz2.getTeam()).thenReturn("Team 20");
		when(platz2.getWappen()).thenReturn(create("proto://wappen-team-20"));
		when(platz2.getSpiele()).thenReturn(21);
		when(platz2.getToreHeim()).thenReturn(25);
		when(platz2.getToreAuswaerts()).thenReturn(26);
		when(platz2.getGegentoreHeim()).thenReturn(27);
		when(platz2.getGegentoreAuswaerts()).thenReturn(28);
		when(platz2.getPunkte()).thenReturn(29);

		when(tabellenService.erstelleTabelle(league, season)).thenReturn(List.of(platz1, platz2));

		this.mockMvc.perform(get("/tabelle/" + league + "/" + season)) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.[0].wappen", is(platz1.getWappen().toASCIIString()))) //
				.andExpect(jsonPath("$.[0].team", is(platz1.getTeam()))) //
				.andExpect(jsonPath("$.[0].spiele", is(platz1.getSpiele()))) //
				.andExpect(jsonPath("$.[0].siege", is(platz1.getAnzahlSiege()))) //
				.andExpect(jsonPath("$.[0].unentschieden", is(platz1.getAnzahlUnentschieden()))) //
				.andExpect(jsonPath("$.[0].niederlagen", is(platz1.getAnzahlNiederlagen()))) //
				.andExpect(jsonPath("$.[0].tore", is(platz1.getTore()))) //
				.andExpect(jsonPath("$.[0].gegentore", is(platz1.getGegentore()))) //
				.andExpect(jsonPath("$.[0].tordifferenz", is(platz1.getTorDifferenz()))) //
				.andExpect(jsonPath("$.[0].punkte", is(platz1.getPunkte()))) //
				.andExpect(jsonPath("$.[0].letzte5", is("NUS--"))) //
				//
				.andExpect(jsonPath("$.[1].wappen", is(platz2.getWappen().toASCIIString()))) //
				.andExpect(jsonPath("$.[1].team", is(platz2.getTeam()))) //
				.andExpect(jsonPath("$.[1].spiele", is(platz2.getSpiele()))) //
				.andExpect(jsonPath("$.[1].siege", is(platz2.getAnzahlSiege()))) //
				.andExpect(jsonPath("$.[1].unentschieden", is(platz2.getAnzahlUnentschieden()))) //
				.andExpect(jsonPath("$.[1].niederlagen", is(platz2.getAnzahlNiederlagen()))) //
				.andExpect(jsonPath("$.[1].tore", is(platz2.getTore()))) //
				.andExpect(jsonPath("$.[1].gegentore", is(platz2.getGegentore()))) //
				.andExpect(jsonPath("$.[1].tordifferenz", is(platz2.getTorDifferenz()))) //
				.andExpect(jsonPath("$.[1].punkte", is(platz2.getPunkte()))) //
				.andExpect(jsonPath("$.[1].letzte5", is("-----"))) //
		;

	}

}
