package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static de.atruvia.ase.samman.buli.infra.adapters.secondary.OpenLigaDbSpieltagRepoMother.wappenFsRepo;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.Test;

class OpenLigaDbWappenRepoTest {

	@Test
	void canRetrieveDataOf2022() throws Exception {
		assertThat(wappenFsRepo().getWappen("bl1", "2022", "Eintracht Frankfurt")).isEqualTo(URI.create(""));
		assertThat(wappenFsRepo().getWappen("bl1", "2022", "FC Bayern München")).isEqualTo(URI.create(""));
	}

}
