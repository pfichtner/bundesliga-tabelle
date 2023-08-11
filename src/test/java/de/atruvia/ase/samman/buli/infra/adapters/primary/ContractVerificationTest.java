package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import de.atruvia.ase.samman.buli.Main;
import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;

@Provider("BundesligaBackend")
@PactFolder("pacts")
@SpringBootTest(classes = Main.class, webEnvironment = RANDOM_PORT)
@IgnoreNoPactsToVerify
class ContractVerificationTest {

	@LocalServerPort
	int port;

	@MockBean
	SpieltagRepo spieltagFakeRepo;

	@Autowired
	TabellenService tabellenService;

	@BeforeEach
	void setup(PactVerificationContext context) {
		if (context != null) {
			context.setTarget(new HttpTestTarget("localhost", port));
		}
	}

	@TestTemplate
	@ExtendWith(PactVerificationInvocationContextProvider.class)
	void verifyContracts(PactVerificationContext context) {
		if (context != null) {
			context.verifyInteraction();
		}
	}

	@State("matchday #3 team has won on matchday #1, draw on matchday #2 and loss on day #3")
	void matchdayThreeWinDrawLoss() throws Exception {
		String team1 = "Team 1";
		String team2 = "Team 2";
		Paarung paarung1 = ergebnis(team1, team2, MAX_VALUE, MIN_VALUE);
		// Es macht tatsächlich einen Unterschied ob Team 1 unentschiden gegen Team 2
		// oder umgekehrt spielt!
//		Paarung paarung2 = ergebnis(team1, team2, MAX_VALUE, MAX_VALUE);
		Paarung paarung2 = ergebnis(team2, team1, MAX_VALUE, MAX_VALUE);
		Paarung paarung3 = ergebnis(team1, team2, MIN_VALUE, MAX_VALUE);
		when(spieltagFakeRepo.lade(anyString(), anyString())).thenReturn(asList(paarung1, paarung2, paarung3));
		assertIsNus(team1);
	}

	void assertIsNus(String team1) {
		// Der Client erwartet: Das oberste Team hat das erste Spiel gewonnen, das
		// zweite unentschieden gespielt und das dritte verloren.
		// Wie die Reihenfolge SUN-- oder --NUS ist, wird durch den TabellenService
		// bestimmt: D.h. wir dürfen nicht den TabellenService mocken, sondern das
		// SpieltagRepo. Nun könnte es aber sein, dass bei Team1 vs Team2 bei 1:0, 1:1
		// und 0:1 Team2 in der Tabelle VOR Team 1 steht (Team1 ist SUN, Team2 ist NUS),
		// wäre Team2 also vor Team1 würden wir hier u.U. etwas verifizieren, was nicht
		// der Erwartung enspricht. Von daher stellen wir hier noch via assert sicher,
		// dass Team1 (das SUN Team) auch das erste Team ist.
		TabellenPlatz entry0 = tabellenService.erstelleTabelle("", "").get(0);
		assert entry0.getTeam().equals(team1) : entry0.getTeam();
		assert entry0.getLetzte(5).equals(List.of(NIEDERLAGE, UNENTSCHIEDEN, SIEG));
	}

	Paarung ergebnis(String team1, String team2, int tore1, int tore2) {
		return Paarung.builder().team1(team1).team2(team2).ergebnis(tore1, tore2).build();
	}
}
