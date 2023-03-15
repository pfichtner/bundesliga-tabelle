package de.atruvia.ase.samman;

import static de.atruvia.ase.samman.TabelleTest.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.TabelleTest.Ergebnis.SIEG;
import static de.atruvia.ase.samman.TabelleTest.Ergebnis.UNENTSCHIEDEN;
import static java.util.Collections.emptyMap;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.TabelleTest.OrdnungsElement;
import lombok.Builder;
import lombok.Value;

class TabelleTest {

	public static class OrdnungsElement {

		private final TabellenPlatz tabellenPlatz;

		public OrdnungsElement(TabellenPlatz tabellenPlatz) {
			this.tabellenPlatz = tabellenPlatz;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			OrdnungsElement other = (OrdnungsElement) obj;
			return tabellenPlatz.gegentore == other.tabellenPlatz.gegentore && tabellenPlatz.punkte == other.tabellenPlatz.punkte
					&& Objects.equals(tabellenPlatz, other.tabellenPlatz) && tore == other.tore;
		}

	}

	@Value
	@Builder
	static class TabellenPlatz {

		static TabellenPlatz NULL = new TabellenPlatz(0, "", 0, emptyMap(), 0, 0, 0);

		int platz;
		String team;
		@Builder.Default
		int spiele = 1;
		Map<Ergebnis, Integer> ergebnisse;
		int punkte;
		int tore;
		int gegentore;

		static class TabellenPlatzBuilder {

			TabellenPlatzBuilder() {
				ergebnisse = new HashMap<>();
			}

			public TabellenPlatz.TabellenPlatzBuilder ergebnis(Ergebnis ergebnis) {
				ergebnisse.merge(ergebnis, 1, (a, b) -> a + b);
				return this;
			}

		}

		public int getTorDifferenz() {
			return tore - gegentore;
		}

		public TabellenPlatz merge(TabellenPlatz other) {
			return TabellenPlatz.builder() //
					.ergebnisse(merge(this.ergebnisse, other.ergebnisse)) //
					.spiele(this.spiele + other.spiele) //
					.punkte(this.punkte + other.punkte) //
					.tore(this.tore + other.tore) //
					.gegentore(this.gegentore + other.gegentore) //
					.build();
		}

		private static Map<Ergebnis, Integer> merge(Map<Ergebnis, Integer> map1, Map<Ergebnis, Integer> map2) {
			Map<Ergebnis, Integer> map = new HashMap<>();
			map.putAll(map1);
			map.putAll(map2);
			return map;
		}

		public int getGewonnen() {
			return ergebnis(SIEG);
		}

		public int getUnentschieden() {
			return ergebnis(UNENTSCHIEDEN);
		}

		public int getVerloren() {
			return ergebnis(NIEDERLAGE);
		}

		private int ergebnis(Ergebnis type) {
			return ergebnisse.getOrDefault(type, 0);
		}

	}

	public enum Ergebnis {
		SIEG, UNENTSCHIEDEN, NIEDERLAGE;
	}

	@Value
	@Builder(toBuilder = true)
	private static class Paarung {
		boolean gespielt;
		String team1, team2;
		int tore, gegentore;

		int punkte() {
			return switch (ergebnis()) {
			case SIEG -> 3;
			case UNENTSCHIEDEN -> 1;
			case NIEDERLAGE -> 0;
			};
		}

		public Ergebnis ergebnis() {
			return tore == gegentore ? UNENTSCHIEDEN : tore > gegentore ? Ergebnis.SIEG : NIEDERLAGE;
		}

		private Paarung swap() {
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
				1|Team 1|0|0|0|0|0|0|0|0
				1|Team 2|0|0|0|0|0|0|0|0""");
	}

	@Test
	void zweiMannschaftenEinSpielKeineTore() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2").ergebnis(0, 0), paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle("""
				1|Team 1|1|0|1|0|1|0|0|0
				1|Team 2|1|0|1|0|1|0|0|0""");
	}

	@Test
	void mannschaftMitMehrPunktenIstWeiterOben() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2").ergebnis(0, 1), paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle("""
				1|Team 2|1|1|0|0|3|1|0|1
				2|Team 1|1|0|0|1|0|0|1|-1""");
	}

	@Test
	void zweiMannschaftenZweiSpieleMitToren() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2").ergebnis(1, 0), paarung("Team 2", "Team 1").ergebnis(1, 0));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle("""
				1|Team 1|2|1|0|1|3|1|1|0
				1|Team 2|2|1|0|1|3|1|1|0""");
	}

	private Paarung.PaarungBuilder paarung(String team1, String team2) {
		return Paarung.builder().team1(team1).team2(team2);
	}

	private void gegenSeienDiePaarungen(Paarung.PaarungBuilder... paarungen) {
		this.paarungen = Arrays.stream(paarungen).map(Paarung.PaarungBuilder::build).toArray(Paarung[]::new);
	}

	private static class Tabelle {

		private final Map<String, TabellenPlatz> eintraege = new HashMap<>();

		private void add(Paarung paarung) {
			addInternal(paarung);
			addInternal(paarung.swap());
		}

		private void addInternal(Paarung paarung) {
			eintraege.merge(paarung.getTeam1(), newEintrag(paarung), TabellenPlatz::merge);
		}

		private TabellenPlatz newEintrag(Paarung paarung) {
			if (!paarung.isGespielt()) {
				return TabellenPlatz.NULL;
			}
			return TabellenPlatz.builder() //
					.ergebnis(paarung.ergebnis()) //
					.punkte(paarung.punkte()) //
					.tore(paarung.tore) //
					.gegentore(paarung.gegentore) //
					.build();
		}

		public List<TabellenPlatz> getEntries() {
			// TODO sort mehr als nur punkte
			// TODO platz enumerating
			Map<OrdnungsElement, List<TabellenPlatz>> a = eintraege.entrySet().stream().map(this::tabellenPlatz)
					.sorted(comparing(TabellenPlatz::getPunkte, reverseOrder()))
					.collect(Collectors.groupingBy(OrdnungsElement::new));
			return eintraege.entrySet().stream().map(this::tabellenPlatz)
					.sorted(comparing(TabellenPlatz::getPunkte, reverseOrder())).collect(toList());
		}

		private TabellenPlatz tabellenPlatz(Entry<String, TabellenPlatz> entry) {
			TabellenPlatz eintrag = entry.getValue();
			return TabellenPlatz.builder().platz(1).team(entry.getKey()) //
					.ergebnisse(eintrag.getErgebnisse()).spiele(eintrag.getSpiele()) //
					.punkte(eintrag.getPunkte()) //
					.tore(eintrag.getTore()) //
					.gegentore(eintrag.getGegentore()) //
					.build();
		}

	}

	private void wennDieTabelleBerechnetWird() {
		Arrays.stream(this.paarungen).forEach(sut::add);
	}

	private void dannIstDieTabelle(String expected) {
		assertThat(toString(sut.getEntries())).isEqualTo(expected);
	}

	private String toString(List<TabellenPlatz> plaetze) {
		return plaetze.stream().map(this::toString).collect(joining("\n"));
	}

	private String toString(TabellenPlatz platz) {
		return Arrays
				.asList(platz.getPlatz(), platz.getTeam(), platz.getSpiele(), platz.getGewonnen(),
						platz.getUnentschieden(), platz.getVerloren(), platz.getPunkte(), platz.getTore(),
						platz.getGegentore(), platz.getTorDifferenz())
				.stream().map(Objects::toString).collect(joining("|"));
	}

}
