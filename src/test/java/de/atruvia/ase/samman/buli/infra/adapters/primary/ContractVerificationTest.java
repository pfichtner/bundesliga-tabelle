package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import de.atruvia.ase.samman.buli.domain.TabellenPlatzMother;
import de.atruvia.ase.samman.buli.domain.ports.primary.TabellenService;

@Provider("BundesligaBackend")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@PactFolder("pacts")
@ContractTest
class ContractVerificationTest {

	@LocalServerPort
	int port;

	@MockBean
	TabellenService tabellenService;

	@BeforeEach
	void setup(PactVerificationContext context) {
		context.setTarget(new HttpTestTarget("localhost", port));
	}

	@TestTemplate
	@ExtendWith(PactVerificationInvocationContextProvider.class)
	void verifyContracts(PactVerificationContext context) {
		context.verifyInteraction();
	}

	@State("matchday #3 team has won on matchday #1, draw on matchday #2 and loss on day #3")
	void matchdayThreeWinDrawLoss() {
		var answer = TabellenPlatzMother.onMatchday3TeamHasWonOnMatchdayNo1ThenDrawOnMatchdayNo2ThenLossOnMatchdayNo3();
		when(tabellenService.erstelleTabelle(anyString(), anyString())).thenReturn(answer);
	}

	@State("team #1 is currently playing")
	void runningGame() {
		var answer = TabellenPlatzMother.team1IsCurrentlyPlaying();
		when(tabellenService.erstelleTabelle(anyString(), anyString())).thenReturn(answer);
	}

}
