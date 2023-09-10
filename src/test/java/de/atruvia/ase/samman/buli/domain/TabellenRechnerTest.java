package de.atruvia.ase.samman.buli.domain;

import static com.google.common.collect.Streams.concat;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.NIEDERLAGE;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.SIEG;
import static de.atruvia.ase.samman.buli.domain.Paarung.Ergebnis.UNENTSCHIEDEN;
import static java.beans.Introspector.getBeanInfo;
import static java.net.URI.create;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.samman.buli.domain.Paarung.PaarungBuilder;

public class TabellenRechnerTest {

	private Paarung[] paarungen;
	private TabellenRechner sut = new TabellenRechner();

	@Test
	void zweiMannschaftenKeinSpiel() {
		gegebenSeienDiePaarungen(PaarungBuilder.paarung("Team 1", "Team 2"),
				PaarungBuilder.paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(
				"""
						platz|team  |spiele|anzahlSiege|anzahlUnentschieden|anzahlNiederlagen|punkte|tore|gegentore|torDifferenz
						1    |Team 1|     0|          0|                  0|                0|     0|   0|        0|           0
						1    |Team 2|     0|          0|                  0|                0|     0|   0|        0|           0""");
	}

	@Test
	void zweiMannschaftenEinSpielKeineTore() {
		gegebenSeienDiePaarungen(PaarungBuilder.paarung("Team 1", "Team 2").ergebnis(0, 0),
				PaarungBuilder.paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(
				"""
						platz|team  |spiele|anzahlSiege|anzahlUnentschieden|anzahlNiederlagen|punkte|tore|gegentore|torDifferenz
						1    |Team 1|     1|          0|                  1|                0|     1|   0|        0|           0
						1    |Team 2|     1|          0|                  1|                0|     1|   0|        0|           0""");
	}

	@Test
	void mannschaftMitMehrPunktenIstWeiterOben() {
		gegebenSeienDiePaarungen(PaarungBuilder.paarung("Team 1", "Team 2").ergebnis(0, 1),
				PaarungBuilder.paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(
				"""
						platz|team  |spiele|anzahlSiege|anzahlUnentschieden|anzahlNiederlagen|punkte|tore|gegentore|torDifferenz
						1    |Team 2|     1|          1|                  0|                0|     3|   1|        0|           1
						2    |Team 1|     1|          0|                  0|                1|     0|   0|        1|          -1""");
	}

	@Test
	void zweiMannschaftenZweiSpieleMitToren() {
		gegebenSeienDiePaarungen( //
				PaarungBuilder.paarung("Team 1", "Team 2").ergebnis(1, 0), //
				PaarungBuilder.paarung("Team 2", "Team 1").ergebnis(1, 0) //
		);
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(
				"""
						platz|team  |spiele|anzahlSiege|anzahlUnentschieden|anzahlNiederlagen|punkte|tore|gegentore|torDifferenz
						1    |Team 1|     2|          1|                  0|                1|     3|   1|        1|           0
						1    |Team 2|     2|          1|                  0|                1|     3|   1|        1|           0""");
	}

	@Test
	void dieFolgendeMannschaftIstPlatzDrei() {
		gegebenSeienDiePaarungen( //
				PaarungBuilder.paarung("Team 1", "Team 2").ergebnis(1, 0), //
				PaarungBuilder.paarung("Team 2", "Team 1").ergebnis(1, 0), //
				PaarungBuilder.paarung("Team 1", "Team 3").ergebnis(1, 0), //
				PaarungBuilder.paarung("Team 2", "Team 3").ergebnis(1, 0) //
		);
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(
				"""
						platz|team  |spiele|anzahlSiege|anzahlUnentschieden|anzahlNiederlagen|punkte|tore|gegentore|torDifferenz
						1    |Team 1|3     |          2|                  0|                1|     6|   2|        1|           1
						1    |Team 2|3     |          2|                  0|                1|     6|   2|        1|           1
						3    |Team 3|2     |          0|                  0|                2|     0|   0|        2|          -2""");
	}

	@Test
	void punktUndTorGleichAberMehrAuswärtsTore() {
		gegebenSeienDiePaarungen( //
				PaarungBuilder.paarung("Team 1", "Team 2").ergebnis(1, 2), //
				PaarungBuilder.paarung("Team 2", "Team 1").ergebnis(0, 1) //
		);
		wennDieTabelleBerechnetWird();
		dannIstDieTabelle(
				"""
						platz|team  |spiele|anzahlSiege|anzahlUnentschieden|anzahlNiederlagen|punkte|tore|gegentore|torDifferenz
						1    |Team 2|     2|          1|                  0|                1|     3|   2|        2|           0
						2    |Team 1|     2|          1|                  0|                1|     3|   2|        2|           0""");
	}

	@Test
	void wappenIstImmerDasDerLetztenPaarung() {
		gegebenSeienDiePaarungen(
				paarung("Team 1", "Team 2", create("proto://wappenAlt1"), create("proto://wappenAlt2")),
				paarung("Team 2", "Team 1", create("proto://wappenNeu2"), create("proto://wappenNeu1")));
		wennDieTabelleBerechnetWird();
		dannSindDieWappen("""
				proto://wappenNeu1
				proto://wappenNeu2""");
	}

	@Test
	void nullWappenWerdenNichtUebernommen() {
		gegebenSeienDiePaarungen(
				paarung("Team 1", "Team 2", create("proto://wappenAlt1"), create("proto://wappenAlt2")),
				paarung("Team 2", "Team 1", create("proto://wappenNeu2"), null));
		wennDieTabelleBerechnetWird();
		dannSindDieWappen("""
				proto://wappenAlt1
				proto://wappenNeu2""");
	}

	@Test
	void wennEinWappenInAllenPaarungenNullIstIstEsNull() {
		gegebenSeienDiePaarungen( //
				paarung("Team 1", "Team 2", create("proto://wappen1"), null), //
				paarung("Team 2", "Team 1", null, create("proto://wappen1")) //
		);
		wennDieTabelleBerechnetWird();
		dannSindDieWappen("""
				proto://wappen1
				null""");
	}

	@Test
	void keineSpieleKeineTendenz() {
		gegebenSeienDiePaarungen(PaarungBuilder.paarung("Team 1", "Team 2"),
				PaarungBuilder.paarung("Team 2", "Team 1"));
		wennDieTabelleBerechnetWird();
		assertThat(sut.getEntries()).satisfiesExactly( //
				e1 -> assertThat(e1.getErgebnisse()).isEmpty(), //
				e2 -> assertThat(e2.getErgebnisse()).isEmpty() //
		);
	}

	@Test
	void zweiSpieleTendenz_dieLetztePaarungIstVorneInDerListe() {
		gegebenSeienDiePaarungen( //
				PaarungBuilder.paarung("Team 1", "Team 2").ergebnis(1, 0), //
				PaarungBuilder.paarung("Team 2", "Team 1").ergebnis(1, 1) //
		);
		wennDieTabelleBerechnetWird();
		assertThat(sut.getEntries()).satisfiesExactly( //
				e1 -> assertThat(e1.getErgebnisse()).containsExactly(SIEG, UNENTSCHIEDEN), //
				e2 -> assertThat(e2.getErgebnisse()).containsExactly(NIEDERLAGE, UNENTSCHIEDEN) //
		);
	}

	private Paarung.PaarungBuilder paarung(String teamHeim, String teamGast, URI wappenHeim, URI wappenGast) {
		return PaarungBuilder.paarung(teamHeim, teamGast).wappenHeim(wappenHeim).wappenGast(wappenGast);
	}

	private void gegebenSeienDiePaarungen(Paarung.PaarungBuilder... paarungen) {
		this.paarungen = stream(paarungen).map(Paarung.PaarungBuilder::build).toArray(Paarung[]::new);
	}

	private void wennDieTabelleBerechnetWird() {
		stream(this.paarungen).forEach(sut::add);
	}

	private void dannIstDieTabelle(String expected) {
		assertThat(print(sut.getEntries())).isEqualTo(line(asList(expected.split("\\|")).stream().map(String::trim)));
	}

	private void dannSindDieWappen(String expected) {
		assertThat(sut.getEntries().stream().map(t -> t.getWappen() == null ? "null" : t.getWappen().toASCIIString())
				.collect(joining("\n"))).isEqualTo(expected);
	}

	private static String print(List<TabellenPlatz> plaetze) {
		List<String> attribs = asList("platz", "team", "spiele", "anzahlSiege", "anzahlUnentschieden",
				"anzahlNiederlagen", "punkte", "tore", "gegentore", "torDifferenz");
		Stream<String> header = Stream.of(line(attribs.stream()));
		Stream<String> values = plaetze.stream().map(t -> print(t, attribs));
		return concat(header, values).collect(joining("\n"));
	}

	private static String print(TabellenPlatz platz, List<String> attribs) {
		return line(values(attribs, platz).stream());
	}

	private static String line(Stream<?> objects) {
		return objects.map(Object::toString).collect(joining("|"));
	}

	private static List<Object> values(List<String> attribs, TabellenPlatz platz) {
		try {
			List<PropertyDescriptor> descriptors = asList(getBeanInfo(platz.getClass()).getPropertyDescriptors());
			return attribs.stream().map(a -> readValue(platz, descriptors, a)).toList();
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	private static Object readValue(Object bean, List<PropertyDescriptor> descriptors, String attribName) {
		Method readMethod = descriptors.stream().filter(p -> p.getName().equals(attribName)).findFirst()
				.orElseThrow(() -> new IllegalStateException("no attribute with name " + attribName)).getReadMethod();
		try {
			return readMethod.invoke(bean);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
