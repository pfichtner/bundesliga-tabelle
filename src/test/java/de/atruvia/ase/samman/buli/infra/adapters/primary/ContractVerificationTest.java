package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
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
import de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;

@Provider("BundesligaBackend")
@PactFolder("pacts")
@SpringBootTest(classes = Main.class, webEnvironment = RANDOM_PORT)
@IgnoreNoPactsToVerify
class ContractVerificationTest {

	@LocalServerPort
	int port;

	@MockBean
	SpieltagRepo spieltagRepoMock;

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
		String teamName = "anyTeamName";
		List<Paarung> paarungen = asList(SIEG, UNENTSCHIEDEN, NIEDERLAGE).stream().map(e -> paarung(teamName, e))
				.collect(toList());
		assert teamHasPlayedThreeMatches(teamName, paarungen);
		assert allOtherThanTeamHasPlayedOneMatch(teamName, paarungen);
		when(spieltagRepoMock.lade(anyString(), anyString())).thenReturn(paarungen);
	}

	private static boolean allOtherThanTeamHasPlayedOneMatch(String teamName, List<Paarung> paarungen) {
		return paarungen.stream().filter(not(team1Is(teamName))).collect(groupingBy(Paarung::getTeam1)).values()
				.stream().allMatch(l -> l.size() == 1);
	}

	private static boolean teamHasPlayedThreeMatches(String teamName, List<Paarung> paarungen) {
		return paarungen.stream().filter(team1Is(teamName)).count() == 3;
	}

	private static Predicate<Paarung> team1Is(String teamName) {
		return p -> p.getTeam1().equals(teamName);
	}

	private static Paarung paarung(String team, Ergebnis ergebnis) {
		String otherTeam = randomTeamOtherThan(team);
		switch (ergebnis) {
		case SIEG:
			return sieg(team, otherTeam);
		case UNENTSCHIEDEN:
			return unentschieden(team, otherTeam);
		case NIEDERLAGE:
			return niederlage(team, otherTeam);
		}
		throw new IllegalStateException("Unknown type " + ergebnis);
	}

	private static String randomTeamOtherThan(String team) {
		return "not(" + team + ")-" + randomUUID();
	}

	private static Paarung niederlage(String team1, String team2) {
		return ergebnis(team1, team2, MIN_VALUE, MAX_VALUE);
	}

	private static Paarung unentschieden(String team1, String team2) {
		return ergebnis(team1, team2, MAX_VALUE, MAX_VALUE);
	}

	private static Paarung sieg(String team1, String team2) {
		return ergebnis(team1, team2, MAX_VALUE, MIN_VALUE);
	}

	private static Paarung ergebnis(String team1, String team2, int tore1, int tore2) {
		return paarung(team1, team2).ergebnis(tore1, tore2).build();
	}

	private static PaarungBuilder paarung(String team1, String team2) {
		return Paarung.builder().team1(team1).team2(team2);
	}

}
