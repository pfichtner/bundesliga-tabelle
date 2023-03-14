package de.atruvia.ase.samman;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.stream.Collector;

import org.junit.jupiter.api.Test;

import lombok.Builder;
import lombok.Value;

class TabelleTest {

	private static final String TEAM_1 = "Team 1";
	private static final String TEAM_2 = "Team 2";

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
	@Builder(toBuilder = true)
	private static class Paarung {
		boolean gespielt;
		String team1, team2;
		int tore, gegentore;

		int punkte() {
			if (!gespielt)
				return 0;
			return tore > gegentore ? 3 : tore == gegentore ? 1 : 0;
		}

		private Paarung reverse() {
			return toBuilder().team1(team2).team2(team1).tore(gegentore).gegentore(tore).build();
		}

		private static class PaarungBuilder {

			public PaarungBuilder score(int tore, int gegentore) {
				this.gespielt = true;
				this.tore = tore;
				this.gegentore = gegentore;
				return this;
			}

		}

	}

	private Paarung[] paarungen;
	private TabellenPlatz[] tabelle;

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegenSeienDiePaarungen(paarung(TEAM_1, TEAM_2), paarung(TEAM_2, TEAM_1));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(tabellenplatz().platz(1).team(TEAM_1), tabellenplatz().platz(1).team(TEAM_2));
		dannIstDieTabelle("""
				1|Team 1|0
				1|Team 2|0
				""");
	}

	@Test
	void zweiMannschaftenEinSpielKeineTore() {
		gegenSeienDiePaarungen(paarung(TEAM_1, TEAM_2).score(0, 0), paarung(TEAM_2, TEAM_1));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle( //
				tabellenplatz().platz(1).team(TEAM_1).punkte(1), //
				tabellenplatz().platz(1).team(TEAM_2).punkte(1) //
		);
	}

	@Test
	void zweiMannschaftenZweiSpieleMitToren() {
		gegenSeienDiePaarungen(paarung(TEAM_1, TEAM_2).score(1, 0), paarung(TEAM_2, TEAM_1).score(1, 0));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle( //
				tabellenplatz().platz(1).team(TEAM_1).punkte(3).tore(1).gegentore(1), //
				tabellenplatz().platz(1).team(TEAM_2).punkte(3).tore(1).gegentore(1) //
		);
	}

	private Paarung.PaarungBuilder paarung(String team1, String team2) {
		return Paarung.builder().team1(team1).team2(team2);
	}

	private TabellenPlatz.TabellenPlatzBuilder tabellenplatz() {
		return TabellenPlatz.builder();
	}

	private void gegenSeienDiePaarungen(Paarung.PaarungBuilder... paarungen) {
		this.paarungen = Arrays.stream(paarungen).map(Paarung.PaarungBuilder::build).toArray(Paarung[]::new);
	}

	private static class T {

		@Value
		@Builder
		static class Entry {
			int punkte;
			int tore;
			int gegentore;

			public Entry merge(Entry other) {
				return Entry.builder() //
						.punkte(this.punkte + other.punkte) //
						.tore(this.tore + other.tore) //
						.gegentore(this.gegentore + other.gegentore) //
						.build();
			}

		}

		private final Map<String, Entry> entries = new HashMap<>();

		void add(Paarung paarung) {
			addInternal(paarung);
			addInternal(paarung.reverse());
		}

		private void addInternal(Paarung paarung) {
			entries.merge(paarung.getTeam1(), new Entry(paarung.punkte(), paarung.tore, paarung.gegentore),
					Entry::merge);
		}

		public List<TabellenPlatz> getEntries() {
			return entries.entrySet().stream().map(this::tabellenPlatz).collect(toList());
		}

		private TabellenPlatz tabellenPlatz(java.util.Map.Entry<String, Entry> entry) {
			return TabellenPlatz.builder().platz(1).team(entry.getKey()) //
					.punkte(entry.getValue().punkte) //
					.tore(entry.getValue().tore) //
					.gegentore(entry.getValue().gegentore) //
					.build();
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

	private void dannIstDieTabelle(String expected) {
		assertThat(toString(tabelle)).isEqualTo(expected);
	}

	private String toString(TabellenPlatz[] t) {
		return Arrays.stream(t).map(e -> Arrays.asList(e.getPlatz(), e.getTeam(), e.getPunkte()).stream()
				.map(Objects::toString).collect(joining("|"))).collect(joining("\n"));
	}

}
