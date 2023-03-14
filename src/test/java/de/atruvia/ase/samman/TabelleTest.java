package de.atruvia.ase.samman;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

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

			public PaarungBuilder ergebnis(int tore, int gegentore) {
				this.gespielt = true;
				this.tore = tore;
				this.gegentore = gegentore;
				return this;
			}

		}

	}

	private Paarung[] paarungen;
	private Tabelle sut = new Tabelle();

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2"), paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle("""
				1|Team 1|0|0|0
				1|Team 2|0|0|0""");
	}

	@Test
	void zweiMannschaftenEinSpielKeineTore() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2").ergebnis(0, 0), paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle("""
				1|Team 1|1|0|0
				1|Team 2|1|0|0""");
	}

	@Test
	void zweiMannschaftenZweiSpieleMitToren() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2").ergebnis(1, 0), paarung("Team 2", "Team 1").ergebnis(1, 0));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle("""
				1|Team 1|3|1|1
				1|Team 2|3|1|1""");
	}

	private Paarung.PaarungBuilder paarung(String team1, String team2) {
		return Paarung.builder().team1(team1).team2(team2);
	}

	private void gegenSeienDiePaarungen(Paarung.PaarungBuilder... paarungen) {
		this.paarungen = Arrays.stream(paarungen).map(Paarung.PaarungBuilder::build).toArray(Paarung[]::new);
	}

	private static class Tabelle {

		@Value
		@Builder
		static class Eintrag {
			
			static Eintrag NULL = Eintrag.builder().build();
			
			int punkte;
			int tore;
			int gegentore;

			public Eintrag merge(Eintrag other) {
				return Eintrag.builder() //
						.punkte(this.punkte + other.punkte) //
						.tore(this.tore + other.tore) //
						.gegentore(this.gegentore + other.gegentore) //
						.build();
			}

		}

		private final Map<String, Eintrag> eintraege = new HashMap<>();

		private void add(Paarung paarung) {
			addInternal(paarung);
			addInternal(paarung.reverse());
		}

		private void addInternal(Paarung paarung) {
			Eintrag value = new Eintrag(paarung.punkte(), paarung.tore, paarung.gegentore);
			eintraege.merge(paarung.getTeam1(), paarung.isGespielt()? value:Eintrag.NULL,
					Eintrag::merge);
		}

		public List<TabellenPlatz> getEntries() {
			// TODO sort
			return eintraege.entrySet().stream().map(this::tabellenPlatz).collect(toList());
		}

		private TabellenPlatz tabellenPlatz(Entry<String, Eintrag> entry) {
			// TODO platz enumerating
			return TabellenPlatz.builder().platz(1).team(entry.getKey()) //
					.punkte(entry.getValue().punkte) //
					.tore(entry.getValue().tore) //
					.gegentore(entry.getValue().gegentore) //
					.build();
		}

	}

	private void wennDieTabelleBerechnetWird() {
		Arrays.stream(this.paarungen).forEach(sut::add);
	}

	private void dannIstDieTabelle(String expected) {
		assertThat(toString(sut.getEntries())).isEqualTo(expected);
	}

	private String toString(List<TabellenPlatz> tabellenPlaetze) {
		return tabellenPlaetze.stream().map(this::toString).collect(joining("\n"));
	}

	private String toString(TabellenPlatz platz) {
		return Arrays
				.asList(platz.getPlatz(), platz.getTeam(), platz.getPunkte(), platz.getTore(), platz.getGegentore())
				.stream().map(Objects::toString).collect(joining("|"));
	}

}
