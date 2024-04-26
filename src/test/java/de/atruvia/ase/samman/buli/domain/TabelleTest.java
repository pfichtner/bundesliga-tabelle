package de.atruvia.ase.samman.buli.domain;

import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static java.net.URI.create;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.Paarung.Entry;
import de.atruvia.ase.samman.buli.domain.Paarung.Entry.EntryBuilder;
import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;

class TabelleTest {

	Tabelle sut = new Tabelle();

	@Test
	void zweiMannschaftenKeinSpiel() {
		sut.add(paarung("Team 1", "Team 2").build());
		sut.add(paarung("Team 2", "Team 1").build());

		assertThat(sut.getEntries()).hasSize(2);

		TabellenPlatz element0 = sut.getEntries().get(0);
		assertThat(element0.platz()).isEqualTo(1);
		assertThat(element0.team()).isEqualTo("Team 1");
		assertThat(element0.spiele()).isEqualTo(0);

		TabellenPlatz element1 = sut.getEntries().get(1);
		assertThat(element1.platz()).isEqualTo(1);
		assertThat(element1.team()).isEqualTo("Team 2");
		assertThat(element1.spiele()).isEqualTo(0);
	}

	@Test
	void zweiMannschaftenEinSpielKeineTore() {
		sut.add(paarung("Team 1", "Team 2").endergebnis(0, 0).build());
		sut.add(paarung("Team 2", "Team 1").build());

		TabellenPlatz element0 = sut.getEntries().get(0);
		assertThat(element0.platz()).isEqualTo(1);
		assertThat(element0.team()).isEqualTo("Team 1");
		assertThat(element0.spiele()).isEqualTo(1);
		assertThat(element0.punkte()).isEqualTo(1);
		assertThat(element0.tore()).isEqualTo(0);

		TabellenPlatz element1 = sut.getEntries().get(1);
		assertThat(element1.platz()).isEqualTo(1);
		assertThat(element1.team()).isEqualTo("Team 2");
		assertThat(element1.spiele()).isEqualTo(1);
		assertThat(element1.punkte()).isEqualTo(1);
		assertThat(element1.tore()).isEqualTo(0);
	}

	@Test
	void mannschaftMitMehrPunktenIstWeiterOben() {
		sut.add(paarung("Team 1", "Team 2").endergebnis(0, 1).build());
		sut.add(paarung("Team 2", "Team 1").build());

		TabellenPlatz element0 = sut.getEntries().get(0);
		assertThat(element0.platz()).isEqualTo(1);
		assertThat(element0.team()).isEqualTo("Team 2");
		assertThat(element0.spiele()).isEqualTo(1);
		assertThat(element0.punkte()).isEqualTo(3);

		TabellenPlatz element1 = sut.getEntries().get(1);
		assertThat(element1.platz()).isEqualTo(2);
		assertThat(element1.team()).isEqualTo("Team 1");
		assertThat(element1.spiele()).isEqualTo(1);
		assertThat(element1.punkte()).isEqualTo(0);
	}

	@Test
	void zweiMannschaftenZweiSpieleMitToren() {
		sut.add(paarung("Team 1", "Team 2").endergebnis(1, 0).build());
		sut.add(paarung("Team 2", "Team 1").endergebnis(1, 0).build());

		TabellenPlatz element0 = sut.getEntries().get(0);
		assertThat(element0.platz()).isEqualTo(1);
		assertThat(element0.team()).isEqualTo("Team 1");
		assertThat(element0.punkte()).isEqualTo(3);

		TabellenPlatz element1 = sut.getEntries().get(1);
		assertThat(element1.platz()).isEqualTo(1);
		assertThat(element1.team()).isEqualTo("Team 2");
		assertThat(element1.punkte()).isEqualTo(3);
	}

	@Test
	void dieFolgendeMannschaftIstPlatzDrei() {
		sut.add(paarung("Team 1", "Team 2").endergebnis(1, 0).build());
		sut.add(paarung("Team 2", "Team 1").endergebnis(1, 0).build());
		sut.add(paarung("Team 1", "Team 3").endergebnis(1, 0).build());
		sut.add(paarung("Team 2", "Team 3").endergebnis(1, 0).build());

		assertThat(sut.getEntries()).hasSize(3);

		assertThat(sut.getEntries().get(0).platz()).isEqualTo(1);
		assertThat(sut.getEntries().get(1).platz()).isEqualTo(1);
		assertThat(sut.getEntries().get(2).platz()).isEqualTo(3);
	}

	@Test
	void punktUndTorGleichAberMehrAuswärtsTore() {
		sut.add(paarung("Team 1", "Team 2").endergebnis(1, 2).build());
		sut.add(paarung("Team 2", "Team 1").endergebnis(0, 1).build());

		assertThat(sut.getEntries().get(0).team()).isEqualTo("Team 2");
		assertThat(sut.getEntries().get(1).team()).isEqualTo("Team 1");
	}

	@Test
	void wappenIstImmerDasDerLetztenPaarung() {
		sut.add(paarung("Team 1", "Team 2", create("proto://wappenAlt1"), create("proto://wappenAlt2")).build());
		sut.add(paarung("Team 2", "Team 1", create("proto://wappenNeu2"), create("proto://wappenNeu1")).build());

		assertThat(sut.getEntries().get(0).wappen()).isEqualTo(create("proto://wappenNeu1"));
		assertThat(sut.getEntries().get(1).wappen()).isEqualTo(create("proto://wappenNeu2"));
	}

	@Test
	void nullWappenWerdenNichtUebernommen() {
		sut.add(paarung("Team 1", "Team 2", create("proto://wappenAlt1"), create("proto://wappenAlt2")).build());
		sut.add(paarung("Team 2", "Team 1", create("proto://wappenNeu2"), null).build());

		assertThat(sut.getEntries().get(0).wappen()).isEqualTo(create("proto://wappenAlt1"));
		assertThat(sut.getEntries().get(1).wappen()).isEqualTo(create("proto://wappenNeu2"));
	}

	@Test
	void wennEinWappenInAllenPaarungenNullIstIstEsNull() {
		sut.add(paarung("Team ohne Wappen", "Team mit Wappen", null, create("proto://wappen")).build());
		sut.add(paarung("Team mit Wappen", "Team ohne Wappen", create("proto://wappen"), null).build());

		TabellenPlatz element0 = sut.getEntries().get(0);
		assertThat(element0.team()).isEqualTo("Team mit Wappen");
		assertThat(element0.wappen()).isEqualTo(create("proto://wappen"));

		TabellenPlatz element1 = sut.getEntries().get(1);
		assertThat(element1.team()).isEqualTo("Team ohne Wappen");
		assertThat(element1.wappen()).isNull();
	}

	@Test
	void beiAenderndemMannschaftsnamenWirdDerLetzteUebernommen() {
		String team1 = "Team 1";
		String team2 = "Team 2";
		var heimAlt = team(team1).identifier(team1);
		var gastAlt = team(team2 + "-A").identifier(team2);

		var heimNeu = team(team1).identifier(team1);
		var gastNeu = team(team2 + "-B").identifier(team2);
		sut.add(paarung(heimAlt, gastAlt).build());
		sut.add(paarung(heimNeu, gastNeu).build());

		assertThat(sut.getEntries().get(0).team()).isEqualTo(team1);
		assertThat(sut.getEntries().get(1).team()).isEqualTo(team2 + "-B");
	}

	@Test
	void beiAenderndemMannschaftsnamenNullWirdNichtUebernommen() {
		var heimAlt = team("Team 1").identifier("Team1");
		var gastAlt = team("Team 2").identifier("Team2");

		var heimNeu = team("Team 1").identifier("Team1");
		var gastNeu = team(null).identifier("Team2");
		sut.add(paarung(heimAlt, gastAlt).build());
		sut.add(paarung(heimNeu, gastNeu).build());

		assertThat(sut.getEntries().get(0).team()).isEqualTo("Team 1");
		assertThat(sut.getEntries().get(1).team()).isEqualTo("Team 2");
	}

	@Test
	void keineSpieleKeineErgebnisse() {
		sut.add(paarung("Team 1", "Team 2").build());
		sut.add(paarung("Team 2", "Team 1").build());

		assertThat(sut.getEntries().get(0).ergebnisse()).isEmpty();
		assertThat(sut.getEntries().get(1).ergebnisse()).isEmpty();
	}

	@Test
	void zweiSpieleErgebnisse_dieLetztePaarungIstVorneInDerListe() {
		sut.add(paarung("Team 1", "Team 2").endergebnis(1, 0).build());
		sut.add(paarung("Team 1", "Team 2").endergebnis(1, 1).build());

		assertThat(sut.getEntries().get(0).ergebnisse()).containsExactly(SIEG, UNENTSCHIEDEN);
		assertThat(sut.getEntries().get(1).ergebnisse()).containsExactly(NIEDERLAGE, UNENTSCHIEDEN);
	}

	@Test
	void laufendeSpieleWerdenAusgewiesen() {
		sut.add(paarung("Team 1", "Team 2").endergebnis(1, 0).build());
		sut.add(paarung("Team 2", "Team 1").zwischenergebnis(2, 1).build());

		TabellenPlatz element0 = sut.getEntries().get(0);
		assertThat(element0.team()).isEqualTo("Team 1");
		assertThat(element0.laufendesSpiel().toreHeim()).isEqualTo(1);
		assertThat(element0.laufendesSpiel().toreGast()).isEqualTo(2);

		TabellenPlatz element1 = sut.getEntries().get(1);
		assertThat(element1.team()).isEqualTo("Team 2");
		assertThat(element1.laufendesSpiel().toreHeim()).isEqualTo(2);
		assertThat(element1.laufendesSpiel().toreGast()).isEqualTo(1);
	}

	@Test
	void alleAttribute() {
		sut.add(paarung("Team 1", "Team 2").endergebnis(0, 1).build());
		sut.add(paarung("Team 2", "Team 1").endergebnis(42, 42).build());

		TabellenPlatz element0 = sut.getEntries().get(0);
		assertThat(element0.platz()).isEqualTo(1);
		assertThat(element0.team()).isEqualTo("Team 2");
		assertThat(element0.punkte()).isEqualTo(4);
		assertThat(element0.spiele()).isEqualTo(2);

		assertThat(element0.auswaerts().tore()).isEqualTo(1);
		assertThat(element0.auswaerts().gegentore()).isEqualTo(0);
		assertThat(element0.heim().tore()).isEqualTo(42);
		assertThat(element0.heim().gegentore()).isEqualTo(42);
		assertThat(element0.ergebnisse()).containsExactly(SIEG, UNENTSCHIEDEN);
		assertThat(element0.siege()).isEqualTo(1);
		assertThat(element0.unentschieden()).isEqualTo(1);
		assertThat(element0.niederlagen()).isEqualTo(0);
		assertThat(element0.tore()).isEqualTo(43);
		assertThat(element0.gegentore()).isEqualTo(42);
		assertThat(element0.torDifferenz()).isEqualTo(1);

		TabellenPlatz element1 = sut.getEntries().get(1);
		assertThat(element1.platz()).isEqualTo(2);
		assertThat(element1.team()).isEqualTo("Team 1");
		assertThat(element1.punkte()).isEqualTo(1);
		assertThat(element1.spiele()).isEqualTo(2);
		assertThat(element1.auswaerts().tore()).isEqualTo(42);
		assertThat(element1.auswaerts().gegentore()).isEqualTo(42);
		assertThat(element1.heim().tore()).isEqualTo(0);
		assertThat(element1.heim().gegentore()).isEqualTo(1);
		assertThat(element1.ergebnisse()).containsExactly(NIEDERLAGE, UNENTSCHIEDEN);
		assertThat(element1.siege()).isEqualTo(0);
		assertThat(element1.unentschieden()).isEqualTo(1);
		assertThat(element1.niederlagen()).isEqualTo(1);
		assertThat(element1.tore()).isEqualTo(42);
		assertThat(element1.gegentore()).isEqualTo(43);
		assertThat(element1.torDifferenz()).isEqualTo(-1);
	}

	private static PaarungBuilder paarung(String teamHeim, String teamGast) {
		return paarung(team(teamHeim), team(teamGast));
	}

	private static PaarungBuilder paarung(String teamHeim, String teamGast, URI wappenHeim, URI wappenGast) {
		return paarung(team(teamHeim).wappen(wappenHeim), team(teamGast).wappen(wappenGast));
	}

	private static EntryBuilder team(String team) {
		return Entry.builder().team(team);
	}

	private static PaarungBuilder paarung(EntryBuilder heim, EntryBuilder gast) {
		return Paarung.builder().heim(heim.build()).gast(gast.build());
	}

}
