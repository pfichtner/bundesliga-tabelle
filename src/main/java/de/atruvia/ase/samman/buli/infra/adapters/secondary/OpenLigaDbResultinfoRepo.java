package de.atruvia.ase.samman.buli.infra.adapters.secondary;

import static java.lang.String.format;
import static java.net.URI.create;
import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.HOURS;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import lombok.ToString;

@Repository
class OpenLigaDbResultinfoRepo {

	private static final String CACHE_NAME = "resultinfosCache";

	@ToString
	private static class AvailableLeague {
		int leagueId;
		String leagueShortcut;
		String leagueSeason;
	}

	@ToString
	static class Resultinfo {

		@ToString
		static class GlobalResultInfo {
			int id;
		}

		int orderId;
		GlobalResultInfo globalResultInfo;
	}

	private final Gson gson = new Gson();
	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Autowired
	private CacheManager cacheManager;

	@Cacheable(value = CACHE_NAME, key = "#league + '_' + #season")
	public List<Resultinfo> getResultinfos(String league, String season) {
		AvailableLeague availableLeague = getAvailableLeague(league, season).orElseThrow(
				() -> new IllegalArgumentException(String.format("League %s, season %s not found", league, season)));
		return getResultinfos(availableLeague.leagueId);
	}

	@Scheduled(fixedDelay = 1, timeUnit = HOURS)
	public void evictCacheEntries() {
		Optional.ofNullable(cacheManager.getCache(CACHE_NAME)).ifPresent(Cache::clear);
	}

	private Optional<AvailableLeague> getAvailableLeague(String leagueShortcut, String leagueSeason) {
		try {
			String body = httpClient
					.send(HttpRequest.newBuilder(create("https://api.openligadb.de/getavailableleagues")).build(),
							BodyHandlers.ofString())
					.body();
			return stream(gson.fromJson(body, AvailableLeague[].class)) //
					.filter(l -> leagueShortcut.equals(l.leagueShortcut)) //
					.filter(l -> leagueSeason.equals(l.leagueSeason)) //
					.findFirst();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Resultinfo> getResultinfos(int leagueId) {
		try {
			String body = httpClient.send(HttpRequest
					.newBuilder(create(format("https://api.openligadb.de/getresultinfos/%s", leagueId))).build(),
					BodyHandlers.ofString()).body();
			Resultinfo[] fromJson = gson.fromJson(body, Resultinfo[].class);
			return stream(fromJson).sorted(comparing(r -> r.globalResultInfo.id)).toList();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}