package de.atruvia.ase.samman.buli.infra.internal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class OpenLigaDbResultinfoRepoCachingTest {

	@Nested
	@SpringBootTest(properties = OpenLigaDbResultinfoRepo.CACHECLEAR + "=60000")
	class LongCacheEvict {
		@SpyBean
		OpenLigaDbResultinfoRepo repoSpy;

		@Test
		void testCacheHits() {
			queryResultinfosThreeTimes("bl1", "2022");
			verify(repoSpy, times(1)).getResultinfos(eq("bl1"), eq("2022"));
			verifyNoMoreInteractionsWithIgnoredCacheEvicts(repoSpy);
		}

		@Test
		void queriesAgainBecauseLeagueDiffers() {
			String season = "2022";
			queryResultinfosThreeTimes("bl1", season);
			queryResultinfosThreeTimes("bl2", season);
			verify(repoSpy, times(1)).getResultinfos(eq("bl1"), eq(season));
			verify(repoSpy, times(1)).getResultinfos(eq("bl2"), eq(season));
			verifyNoMoreInteractionsWithIgnoredCacheEvicts(repoSpy);
		}

		@Test
		void queriesAgainBecauseSeasonDiffers() {
			String league = "bl1";
			queryResultinfosThreeTimes(league, "2022");
			queryResultinfosThreeTimes(league, "2023");
			verify(repoSpy, times(1)).getResultinfos(eq(league), eq("2022"));
			verify(repoSpy, times(1)).getResultinfos(eq(league), eq("2023"));
			verifyNoMoreInteractionsWithIgnoredCacheEvicts(repoSpy);
		}

		private void queryResultinfosThreeTimes(String league, String season) {
			for (int i = 0; i < 3; i++) {
				repoSpy.getResultinfos(league, season);
			}
		}

	}

	@Nested
	@SpringBootTest(properties = OpenLigaDbResultinfoRepo.CACHECLEAR + "=1000")
	class ShortCacheEvict {
		@SpyBean
		OpenLigaDbResultinfoRepo repoSpy;

		@Test
		void cacheGetsEvictedAfterOneSecond() throws InterruptedException {
			String league = "bl1";
			String season = "2023";
			queryResultinfosThreeTimes(league, season);
			TimeUnit.SECONDS.sleep(2);
			queryResultinfosThreeTimes(league, season);
			verify(repoSpy, times(2)).getResultinfos(eq(league), eq(season));
			verifyNoMoreInteractionsWithIgnoredCacheEvicts(repoSpy);
		}

		private void queryResultinfosThreeTimes(String league, String season) {
			for (int i = 0; i < 3; i++) {
				repoSpy.getResultinfos(league, season);
			}
		}
	}

	private void verifyNoMoreInteractionsWithIgnoredCacheEvicts(OpenLigaDbResultinfoRepo repoSpy) {
		verify(repoSpy, atLeast(0)).evictCacheEntries();
		verifyNoMoreInteractions(repoSpy);
	}

}
