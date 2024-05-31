package de.atruvia.ase.samman.buli.infra.adapters.primary;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder.paarung;
import static de.atruvia.ase.samman.buli.domain.PaarungMother.createPaarungen;
import static de.atruvia.ase.samman.buli.domain.TabellenPlatzMother.onMatchday3TeamHasWonOnMatchdayNo1ThenDrawOnMatchdayNo2ThenLossOnMatchdayNo3;
import static de.atruvia.ase.samman.buli.domain.TabellenPlatzMother.team1IsCurrentlyPlaying;
import static de.atruvia.ase.samman.buli.util.Streams.toOnlyElement;
import static java.lang.Integer.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.primary.DefaultTabellenService;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;

class ContractVerificationDomainBehaviourTest {

	String team1 = "Team 1";

	@Test
	void tendenz() {
		SpieltagRepo spieltagRepo = (league, season) -> createPaarungen(team1, SIEG, UNENTSCHIEDEN, NIEDERLAGE);
		var expectedPlatz1 = onMatchday3TeamHasWonOnMatchdayNo1ThenDrawOnMatchdayNo2ThenLossOnMatchdayNo3().get(0);
		var actualPlatz1 = new DefaultTabellenService(spieltagRepo).erstelleTabelle("anyLeague", "anySeason").get(0);
		assertThat(actualPlatz1.tendenz()).isEqualTo(expectedPlatz1.tendenz());
	}

	@Test
	void runningGame() {
		SpieltagRepo spieltagRepo = (league, season) -> paarungen(
				paarung(team1, "Team 4").zwischenergebnis(0, MAX_VALUE), //
				paarung("Team 2", "Team 3").endergebnis(0, 0) //
		);
		var expectedTeam1Entry = team1IsCurrentlyPlaying().stream().filter(isTeam(team1)).reduce(toOnlyElement()).get();
		var actualTeam1Entry = new DefaultTabellenService(spieltagRepo).erstelleTabelle("anyLeague", "anySeason")
				.stream().filter(isTeam(team1)).reduce(toOnlyElement()).get();
		assertThat(actualTeam1Entry.laufendesSpiel()).isEqualTo(expectedTeam1Entry.laufendesSpiel());
	}

	private static Predicate<TabellenPlatz> isTeam(String name) {
		return t -> name.equals(t.teamName());
	}

	private static List<Paarung> paarungen(PaarungBuilder... paarungen) {
		return Stream.of(paarungen).map(PaarungBuilder::build).toList();
	}

}
