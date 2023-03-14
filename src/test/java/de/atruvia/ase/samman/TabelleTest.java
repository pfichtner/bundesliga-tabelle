package de.atruvia.ase.samman;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import lombok.Builder;
import lombok.Value;

class TabelleTest {

	@Value
	@Builder
	private static class TabellenPlatz {
		int platz;
		String team;
		int punkte;
		int tore;
	}

	@Value
	@Builder
	private static class Paarung {
		String team1, team2;
		int score1, score2;

		int points1() {
			return score1 > score2 ? 3 : score1 < score2 ? 0 : 1;
		}

		int points2() {
			return score2 > score1 ? 3 : score2 < score1 ? 0 : 1;
		}

		private static class PaarungBuilder {

			public PaarungBuilder score(int score1, int score2) {
				this.score1 = score1;
				this.score2 = score2;
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
	void zweiMannschaftenEinSpiel() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2").score(0, 0), paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(tabellenplatz().platz(1).team("Team 1").punkte(1),
				tabellenplatz().platz(2).team("Team 2").punkte(1));
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

	private void wennDieTabelleBerechnetWird() {
		class T {

			class Entry {

				private int points;
				private int goalScored;
				private int goalsGet;

				public void add(int points, int goalScored, int goalsGet) {
					this.points += points;
					this.goalScored += goalScored;
					this.goalsGet += goalsGet;
				}

			}

			private final Map<String, Entry> entries = new HashMap<>();

			void add(Paarung p) {
				Entry entry = entries.computeIfAbsent(p.getTeam1(), k -> new Entry());
				entry.add(p.points1(), p.score1, p.score2);
				entries.put(p.getTeam1(), entry);

			}

		}
		T t = new T();
		Arrays.stream(this.paarungen).forEach(t::add);

		tabelle = new TabellenPlatz[] { //
				tabellenplatz().platz(1).team(this.paarungen[0].team1).build(), //
				tabellenplatz().platz(2).team(this.paarungen[0].team2).build() //
		};
	}

	private void dannIstDieTabelle(TabellenPlatz.TabellenPlatzBuilder... expected) {
		assertThat(tabelle).isEqualTo(
				Arrays.stream(expected).map(TabellenPlatz.TabellenPlatzBuilder::build).toArray(TabellenPlatz[]::new));
	}

}
