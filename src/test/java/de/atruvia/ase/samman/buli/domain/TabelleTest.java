package de.atruvia.ase.samman.buli.domain;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

class TabelleTest {

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

	@Test
	void punktUndTorGleichAberMehrAuswärtsTore() {
		gegenSeienDiePaarungen(paarung("Team 1", "Team 2").ergebnis(1, 2), paarung("Team 2", "Team 1").ergebnis(0, 1));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle("""
				1|Team 2|2|1|0|1|3|2|2|0
				2|Team 1|2|1|0|1|3|2|2|0""");
	}

	@Test
	void wappenIstImmerDasDerLetztenPaarung() {
		gegenSeienDiePaarungen(
				paarung("Team 1", "Team 2", URI.create("proto://wappenAlt1"), URI.create("proto://wappenAlt2")),
				paarung("Team 2", "Team 1", URI.create("proto://wappenNeu1"), URI.create("proto://wappenNeu2")));
		wennDieTabelleBerechnetWird();
		List<TabellenPlatz> entries = sut.getEntries();
		assertThat(entries.stream().map(t -> t.getWappen().toASCIIString()).collect(joining("\n")))
				.isEqualTo("""
						"proto://wappenNeu1"
						"proto://wappenNeu2"""");
	}

	private Paarung.PaarungBuilder paarung(String team1, String team2) {
		return Paarung.builder().team1(team1).team2(team2);
	}

	private Paarung.PaarungBuilder paarung(String team1, String team2, URI wappen1, URI wappen2) {
		return paarung(team1, team2).wappen1(wappen1).wappen2(wappen2);
	}

	private void gegenSeienDiePaarungen(Paarung.PaarungBuilder... paarungen) {
		this.paarungen = Arrays.stream(paarungen).map(Paarung.PaarungBuilder::build).toArray(Paarung[]::new);
	}

	private void wennDieTabelleBerechnetWird() {
		Arrays.stream(this.paarungen).forEach(sut::add);
	}

	private void dannIstDieTabelle(String expected) {
		assertThat(print(sut.getEntries())).isEqualTo(expected);
	}

	private String print(List<TabellenPlatz> plaetze) {
		return plaetze.stream().map(this::print).collect(joining("\n"));
	}

	private String print(TabellenPlatz platz) {
		return Arrays
				.asList(platz.getPlatz(), platz.getTeam(), platz.getSpiele(), platz.getSiege(),
						platz.getUnentschieden(), platz.getNiederlagen(), platz.getPunkte(), platz.getTore(),
						platz.getGegentore(), platz.getTorDifferenz())
				.stream().map(Objects::toString).collect(joining("|"));
	}

}
