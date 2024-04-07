package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class OpenLigaDbResultinfoRepoTest {

	@SpyBean
	OpenLigaDbResultinfoRepo repoSpy;

	@Test
	void testCacheHits() {
		for (int i = 0; i < 3; i++) {
			repoSpy.getResultinfos("bl1", "2023");
		}
		verify(repoSpy, times(1)).getResultinfos(anyString(), anyString());
	}

}
