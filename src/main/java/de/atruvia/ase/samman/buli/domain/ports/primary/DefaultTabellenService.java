package de.atruvia.ase.samman.buli.domain.ports.primary;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import de.atruvia.ase.samman.buli.domain.Paarung;
import de.atruvia.ase.samman.buli.domain.Tabelle;
import de.atruvia.ase.samman.buli.domain.TabellenPlatz;
import de.atruvia.ase.samman.buli.domain.ports.secondary.SpieltagRepo;
import de.atruvia.ase.samman.buli.domain.ports.secondary.TeamRepo;

@Service
class DefaultTabellenService implements TabellenService {

	private final SpieltagRepo spieltagRepo;
	private final TeamRepo wappenRepo;

	public DefaultTabellenService(SpieltagRepo spieltagRepo, TeamRepo wappenRepo) {
		this.spieltagRepo = spieltagRepo;
		this.wappenRepo = new TeamRepo() {

			private final Map<String, URI> cache = new HashMap<>();

			@Override
			public URI getTeams(String league, String season, String team) throws Exception {
				return getTeams(league, season);
			}

			@Override
			public URI getTeams(String league, String season) throws Exception {
				return cache.computeIfAbsent(team, t -> load(wappenRepo, league, season, team));
			}

			private URI load(TeamRepo wappenRepo, String league, String season, String team) {
				try {
					return wappenRepo.getTeams(league, season);
				} catch (Exception e) {
					// TODO Log error
					e.printStackTrace();
					return null;
				}
			}
		};
	}

	@Override
	public List<TabellenPlatz> erstelleTabelle(String league, String season) {
		Tabelle tabelle = new Tabelle();
		lade(league, season).forEach(tabelle::add);
		return tabelle.getEntries();
	}

	

	private List<Paarung> lade(String league, String season) {
		try {
			return spieltagRepo.lade(league, season);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
