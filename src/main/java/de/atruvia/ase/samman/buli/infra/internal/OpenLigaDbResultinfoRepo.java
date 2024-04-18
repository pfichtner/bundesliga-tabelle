package de.atruvia.ase.samman.buli.infra.internal;

import static de.atruvia.ase.samman.buli.infra.internal.OpenLigaDbResultinfoRepo.Resultinfo.byGlobalResultId;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.HOURS;
import static lombok.AccessLevel.PUBLIC;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Repository
@RequiredArgsConstructor
public class OpenLigaDbResultinfoRepo {

	private static final String CACHE_NAME = "resultinfosCache";

	@ToString
	@FieldDefaults(level = PUBLIC)
	private static class AvailableLeague {
		int leagueId;
		String leagueShortcut;
		String leagueSeason;
	}

	@ToString
	@FieldDefaults(level = PUBLIC)
	public static class Resultinfo {

		@ToString
		@FieldDefaults(level = PUBLIC)
		public static class GlobalResultInfo {
			int id;
		}

		public static Resultinfo getEndergebnisType(List<Resultinfo> resultinfos) {
			return last(resultinfos);
		}

		private static <T> T last(List<T> list) {
			return list.get(list.size() - 1);
		}

		public static Comparator<Resultinfo> byGlobalResultId = comparing(r -> r.globalResultInfo.id);

		int id;
		String name;
		int orderId;
		GlobalResultInfo globalResultInfo;
	}

	private final RestTemplate restTemplate;

	@Autowired
	private CacheManager cacheManager;

	@Cacheable(value = CACHE_NAME, key = "#league + '_' + #season")
	public List<Resultinfo> getResultinfos(String league, String season) {
		AvailableLeague availableLeague = getAvailableLeague(league, season).orElseThrow(
				() -> new IllegalArgumentException("League %s, season %s not found".formatted(league, season)));
		return getResultinfos(availableLeague.leagueId);
	}

	@Scheduled(fixedDelay = 1, timeUnit = HOURS)
	public void evictCacheEntries() {
		Optional.ofNullable(cacheManager.getCache(CACHE_NAME)).ifPresent(Cache::clear);
	}

	private Optional<AvailableLeague> getAvailableLeague(String leagueShortcut, String leagueSeason) {
		return stream(
				restTemplate.getForObject("https://api.openligadb.de/getavailableleagues", AvailableLeague[].class)) //
				.filter(l -> leagueShortcut.equals(l.leagueShortcut)) //
				.filter(l -> leagueSeason.equals(l.leagueSeason)) //
				.findFirst();
	}

	private List<Resultinfo> getResultinfos(int leagueId) {
		return stream(restTemplate.getForObject("https://api.openligadb.de/getresultinfos/{leagueId}",
				Resultinfo[].class, leagueId)).sorted(byGlobalResultId).toList();
	}

}