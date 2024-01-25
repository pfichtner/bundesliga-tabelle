package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.PaarungMother.paarungen;
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
import de.atruvia.ase.samman.buli.Main;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;

@Provider("BundesligaBackend")
@PactFolder("pacts")
@SpringBootTest(classes = Main.class, webEnvironment = RANDOM_PORT)
@ContractTest
class ContractVerificationTest {

	@LocalServerPort
	int port;

	@MockBean
	SpieltagRepo spieltagRepoMock;

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
	void matchdayThreeWinDrawLoss() throws Exception {
		when(spieltagRepoMock.lade(anyString(), anyString()))
				.thenReturn(paarungen("anyTeamName", SIEG, UNENTSCHIEDEN, NIEDERLAGE));
	}

}
