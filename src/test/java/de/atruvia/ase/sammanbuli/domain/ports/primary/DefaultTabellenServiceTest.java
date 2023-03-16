package de.atruvia.ase.sammanbuli.domain.ports.primary;

import static de.atruvia.ase.sammanbuli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.readFromLocalFilesystemRepo;
import static java.util.stream.Collectors.joining;
import static org.approvaltests.Approvals.verify;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.sammanbuli.domain.TabellenPlatz;

class DefaultTabellenServiceTest {

	@Test
	void tabelleBl12022Spieltag24() {
		TabellenService sut = new DefaultTabellenService(readFromLocalFilesystemRepo());
		List<TabellenPlatz> erstelleTabelle = sut.erstelleTabelle("bl1", "2022");
		String tabelle = erstelleTabelle.stream().map(f -> print(f, longestTeamName(erstelleTabelle)))
				.collect(joining("\n"));
		verify(tabelle);
	}

	private int longestTeamName(List<TabellenPlatz> erstelleTabelle) {
		return erstelleTabelle.stream().map(p -> p.getTeam()).mapToInt(String::length).max().orElse(0);
	}

	private String print(TabellenPlatz tabellenPlatz, int length) {

		String format = String.format("%-" + (length + 1) + "s", tabellenPlatz.getTeam());

		return java.util.Arrays
				.asList(format, tabellenPlatz.getSpiele(), tabellenPlatz.getGewonnen(),
						tabellenPlatz.getUnentschieden(), tabellenPlatz.getVerloren(), tabellenPlatz.getTore(),
						tabellenPlatz.getGegentore(), tabellenPlatz.getTorDifferenz(), tabellenPlatz.getPunkte())
				.stream().map(o->format(o)).collect(joining("\t|"));
	}

	private String format(Object o) {
		if (o instanceof Number) {
			return String.format("%3d", o);
		}
		return Objects.toString(o);
	}

}
