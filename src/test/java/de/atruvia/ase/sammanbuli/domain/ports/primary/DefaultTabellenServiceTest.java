package de.atruvia.ase.sammanbuli.domain.ports.primary;

import static de.atruvia.ase.sammanbuli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.*;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import de.atruvia.ase.sammanbuli.domain.ports.secondary.SpieltagRepo;

class DefaultTabellenServiceTest {

	@Test
	void test() {
		SpieltagRepo repo = readFromLocalFilesystemRepo();
		DefaultTabellenService sut = new DefaultTabellenService(repo);
		fail("Not yet implemented");
	}

}
