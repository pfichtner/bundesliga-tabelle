package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.TabellenPlatzMother.platzWith;
import static java.net.URI.create;
import static org.hamcrest.CoreMatchers.is;
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
import de.atruvia.ase.samman.buli.domain.TabellenPlatz.TabellenPlatzBuilder;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService.Tabelle;

@SpringBootTest
@AutoConfigureMockMvc
class TabellenHttpAdapterTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TabellenService tabellenService;

	@Test
	void shouldReturnDefaultMessage() throws Exception {
		String liga = "bl1";
		String saison = "2022";
		String saisonName = "Saison Name";

		TabellenPlatz platz1 = platzWithBase(10, platzWith(SIEG, UNENTSCHIEDEN, NIEDERLAGE).toBuilder());
		TabellenPlatz platz2 = platzWithBase(20, platzWith().toBuilder());
		when(tabellenService.erstelleTabelle(liga, saison)).thenReturn(new Tabelle() {
			public String name() {
				return saisonName;
			}

			public List<TabellenPlatz> eintraege() {
				return List.of(platz1, platz2);
			}
		});

		// TODO Streng genommen testen wir hier auch wieder mehr als wir sollten, denn
		// wir testen hier auch wieder die TabellenPlatz::merge Funktionalität mit ab
		// und ob "int getTorDifferenz() { return getTore() - getGegentore(); }" richtig
		// ist.
		// Eigentlich sollte für TabellenPlatz ein Test-Double genutzt werden. Es muss
		// dann jedoch sichergestellt werden, dass die Reihenfolge der "ergebnisse" im
		// Test-Double bei S,U,N der Reihenfolge von TabellenPlatz ::merge entspricht
		this.mockMvc.perform(get("/tabelle/" + liga + "/" + saison)) //
				.andDo(print()) //
				.andExpect(status().isOk()) //
				.andExpect(jsonPath("$.saison.name", is(saisonName))) //
				.andExpect(jsonPath("$.eintrag[0].wappen", is(platz1.getWappen().toASCIIString()))) //
				.andExpect(jsonPath("$.eintrag[0].team", is(platz1.getTeam()))) //
				.andExpect(jsonPath("$.eintrag[0].spiele", is(platz1.getSpiele()))) //
				.andExpect(jsonPath("$.eintrag[0].siege", is(platz1.getAnzahlSiege()))) //
				.andExpect(jsonPath("$.eintrag[0].unentschieden", is(platz1.getAnzahlUnentschieden()))) //
				.andExpect(jsonPath("$.eintrag[0].niederlagen", is(platz1.getAnzahlNiederlagen()))) //
				.andExpect(jsonPath("$.eintrag[0].tore", is(platz1.getTore()))) //
				.andExpect(jsonPath("$.eintrag[0].gegentore", is(platz1.getGegentore()))) //
				.andExpect(jsonPath("$.eintrag[0].tordifferenz", is(platz1.getTorDifferenz()))) //
				.andExpect(jsonPath("$.eintrag[0].punkte", is(platz1.getPunkte()))) //
				.andExpect(jsonPath("$.eintrag[0].letzte5", is("NUS--"))) //
				//
				.andExpect(jsonPath("$.eintrag[1].wappen", is(platz2.getWappen().toASCIIString()))) //
				.andExpect(jsonPath("$.eintrag[1].team", is(platz2.getTeam()))) //
				.andExpect(jsonPath("$.eintrag[1].spiele", is(platz2.getSpiele()))) //
				.andExpect(jsonPath("$.eintrag[1].siege", is(platz2.getAnzahlSiege()))) //
				.andExpect(jsonPath("$.eintrag[1].unentschieden", is(platz2.getAnzahlUnentschieden()))) //
				.andExpect(jsonPath("$.eintrag[1].niederlagen", is(platz2.getAnzahlNiederlagen()))) //
				.andExpect(jsonPath("$.eintrag[1].tore", is(platz2.getTore()))) //
				.andExpect(jsonPath("$.eintrag[1].gegentore", is(platz2.getGegentore()))) //
				.andExpect(jsonPath("$.eintrag[1].tordifferenz", is(platz2.getTorDifferenz()))) //
				.andExpect(jsonPath("$.eintrag[1].punkte", is(platz2.getPunkte()))) //
				.andExpect(jsonPath("$.eintrag[1].letzte5", is("-----"))) //
		;

	}

	private static TabellenPlatz platzWithBase(int base, TabellenPlatzBuilder builder) {
		int cnt = 0;
		return builder.wappen(create("proto://wappen-team-" + base)).team("Team " + base).spiele(base + (++cnt))
				.toreHeim(base + (++cnt)).toreAuswaerts(base + (++cnt)).gegentoreHeim(base + (++cnt))
				.gegentoreAuswaerts(base + (++cnt)).punkte(base + (++cnt)).build();
	}

}
