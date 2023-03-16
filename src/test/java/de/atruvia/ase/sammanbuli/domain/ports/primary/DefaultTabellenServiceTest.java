package de.atruvia.ase.sammanbuli.domain.ports.primary;

import static de.atruvia.ase.sammanbuli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.readFromLocalFilesystemRepo;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.sammanbuli.domain.TabellenPlatz;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;

class DefaultTabellenServiceTest {

	@Test
	void test() {
		TabellenService sut = new DefaultTabellenService(readFromLocalFilesystemRepo());
		List<TabellenPlatz> erstelleTabelle = sut.erstelleTabelle("bl1", "2022");

		String s = erstelleTabelle.stream().map(f -> print(f)).collect(Collectors.joining("\n"));

		fail("Not yet implemented");
	}

	private String print(TabellenPlatz tabellenPlatz) {
		// TODO Auto-generated method stub
		return null;
	}

}
