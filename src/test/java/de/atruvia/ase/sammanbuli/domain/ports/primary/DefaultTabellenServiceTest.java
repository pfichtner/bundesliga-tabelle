package de.atruvia.ase.sammanbuli.domain.ports.primary;

import static de.atruvia.ase.sammanbuli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.readFromLocalFilesystemRepo;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.sammanbuli.domain.TabellenPlatz;
import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;

class DefaultTabellenServiceTest {

	@Test
	void test() {
		TabellenService sut = new DefaultTabellenService(readFromLocalFilesystemRepo());
		List<TabellenPlatz> erstelleTabelle = sut.erstelleTabelle("bl1", "2022");
		
		fail("Not yet implemented");
	}

}
