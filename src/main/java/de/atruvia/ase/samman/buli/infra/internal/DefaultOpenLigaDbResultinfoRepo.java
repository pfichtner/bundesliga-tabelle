package de.atruvia.ase.samman.buli.infra.internal;

import static java.util.Arrays.stream;
import static lombok.AccessLevel.PUBLIC;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Repository
@RequiredArgsConstructor
public class DefaultOpenLigaDbResultinfoRepo implements OpenLigaDbResultinfoRepo {

	@ToString
	@FieldDefaults(level = PUBLIC)
	private static class AvailableLeague {
		int leagueId;
		String leagueShortcut;
		String leagueSeason;
	}

	private final RestTemplate restTemplate;

	public List<Resultinfo> getResultinfos(String league, String season) {
		AvailableLeague availableLeague = getAvailableLeague(league, season).orElseThrow(
				() -> new IllegalArgumentException("League %s, season %s not found".formatted(league, season)));
		return getResultinfos(availableLeague.leagueId);
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
				Resultinfo[].class, leagueId)).toList();
	}

}