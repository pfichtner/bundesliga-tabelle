package de.atruvia.ase.samman;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

class TabelleTest {

	@Value
	@Builder
	private static class TabellenPlatz {
		int platz;
		String team;
		int punkte;
		int tore;
		int gegentore;
	}

	@Value
	@Builder
	private static class Paarung {
		String team1, team2;
		boolean wayPlayed;
		int tore, gegentore;

		int punkte() {
			if (!wayPlayed)
				return 0;
			return tore > gegentore ? 3 : tore < gegentore ? 0 : 1;
		}
		
		@Deprecated
		int points1() {
			if (!wayPlayed)
				return 0;
			return tore > gegentore ? 3 : tore < gegentore ? 0 : 1;
		}

		@Deprecated
		int points2() {
			if (!wayPlayed)
				return 0;
			return gegentore > tore ? 3 : gegentore < tore ? 0 : 1;
		}
		
		private Paarung reverse() {
			return Paarung.builder().team1(team2).team2(team1).tore(gegentore).gegentore(tore).build();
		}

		private static class PaarungBuilder {

			public PaarungBuilder score(int score1, int score2) {
				this.wayPlayed = true;
				this.tore = score1;
				this.gegentore = score2;
				return this;
			}

		}

	}

	private Paarung[] paarungen;
	private TabellenPlatz[] tabelle;

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2"), paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(tabellenplatz().platz(1).team("Team 1"), tabellenplatz().platz(2).team("Team 2"));
	}

	@Test
	void zweiMannschaftenEinSpielKeineTore() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2").score(0, 0), paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle( //
				tabellenplatz().platz(1).team("Team 1").punkte(1), //
				tabellenplatz().platz(2).team("Team 2").punkte(1) //
		);
	}

	@Test
	void zweiMannschaftenZweiSpieleMitToren() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2").score(1, 0), paarung("Team 2", "Team 1").score(1, 0));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle( //
				tabellenplatz().platz(1).team("Team 1").punkte(3).tore(1).gegentore(1), //
				tabellenplatz().platz(2).team("Team 2").punkte(3).tore(1).gegentore(1) //
		);
	}

	private de.atruvia.ase.samman.TabelleTest.Paarung.PaarungBuilder paarung(String team1, String team2) {
		return Paarung.builder().team1(team1).team2(team2);
	}

	private de.atruvia.ase.samman.TabelleTest.TabellenPlatz.TabellenPlatzBuilder tabellenplatz() {
		return TabellenPlatz.builder();
	}

	private void gegenSeienDiePaarungen(Paarung.PaarungBuilder... paarungen) {
		this.paarungen = Arrays.stream(paarungen).map(Paarung.PaarungBuilder::build).toArray(Paarung[]::new);
	}

	private static class T {

		@NoArgsConstructor
		@AllArgsConstructor
		class Entry {

			private int punkte;
			private int tore;
			private int gegentore;

			public Entry add(int punkte, int tore, int gegentore) {
				this.punkte += punkte;
				this.tore += tore;
				this.gegentore += gegentore;
				return this;
			}

		}

		private final Map<String, Entry> entries = new HashMap<>();

		void add(Paarung paarung) {
			entries.merge(paarung.getTeam1(), new Entry(paarung.punkte(), paarung.tore, paarung.gegentore),
					(e1, e2) -> e1.add(e2.punkte, e2.tore, e2.gegentore));
			paarung = paarung.reverse();
			entries.merge(paarung.getTeam1(), new Entry(paarung.punkte(), paarung.tore, paarung.gegentore),
					(e1, e2) -> e1.add(e2.punkte, e2.tore, e2.gegentore));
			
		}

		public List<TabellenPlatz> getEntries() {
			AtomicInteger platz = new AtomicInteger();
			return entries.entrySet().stream()
					.map(e -> TabellenPlatz.builder().platz(platz.incrementAndGet()).team(e.getKey()) //
							.punkte(e.getValue().punkte) //
							.tore(e.getValue().tore) //
							.gegentore(e.getValue().gegentore) //
							.build())
					.collect(toList());
		}

	}

	private void wennDieTabelleBerechnetWird() {
		T t = new T();
		Arrays.stream(this.paarungen).forEach(t::add);
		tabelle = t.getEntries().toArray(TabellenPlatz[]::new);
	}

	private void dannIstDieTabelle(TabellenPlatz.TabellenPlatzBuilder... expected) {
		assertThat(tabelle).isEqualTo(
				Arrays.stream(expected).map(TabellenPlatz.TabellenPlatzBuilder::build).toArray(TabellenPlatz[]::new));
	}

}
