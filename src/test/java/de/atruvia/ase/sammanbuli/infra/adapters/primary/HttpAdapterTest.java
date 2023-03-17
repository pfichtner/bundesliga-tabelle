package de.atruvia.ase.sammanbuli.infra.adapters.primary;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
		TabellenPlatz p1 = TabellenPlatz.builder().team("Team 10").platz(11).spiele(12).punkte(13).toreHeim(14)
				.toreAuswaerts(15).build();
		TabellenPlatz p2 = TabellenPlatz.builder().team("Team 20").platz(21).spiele(22).punkte(23).toreHeim(24)
				.toreAuswaerts(25).build();
		Mockito.when(tabellenService.erstelleTabelle("bl1", "2022")).thenReturn(List.of(p1, p2));

		this.mockMvc.perform(get("/tabelle/bl1/2022")) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.[0].team", is("Team 10"))) //
		;
	}

}
