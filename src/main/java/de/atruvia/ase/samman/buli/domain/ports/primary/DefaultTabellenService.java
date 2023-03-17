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
import de.atruvia.ase.samman.buli.domain.ports.secondary.WappenRepo;

@Service
class DefaultTabellenService implements TabellenService {

	private final SpieltagRepo spieltagRepo;
	private final WappenRepo wappenRepo;
	
	

	public DefaultTabellenService(SpieltagRepo spieltagRepo, WappenRepo wappenRepo) {
		this.spieltagRepo = spieltagRepo;
		this.wappenRepo = new WappenRepo() {
			
			Map<String, URI> cache = new HashMap<>();
			
			@Override
			public URI getWappen(String league, String season, String team) throws Exception {
				return cache.computeIfAbsent(team, t->load(wappenRepo, league, season, team));
			}

			private URI load(WappenRepo wappenRepo, String league, String season, String team) {
				try {
					return wappenRepo.getWappen(league, season, team);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}

	@Override
	public List<TabellenPlatz> erstelleTabelle(String league, String season) {
		Tabelle tabelle = new Tabelle(wappenRepo);
		lade(league, season).forEach(tabelle::add);
		return tabelle.getEntries().stream().map(tp -> tp.withWappen(wappen(tp))).collect(toList());
	}

	private URI wappen(TabellenPlatz tp) {
		try {
			// TODO hart kodierte Daten
			return wappenRepo.getWappen("bl1", "2022", tp.getTeam());
		} catch (Exception e) {
			// TODO wenn das Wappen nicht geladen werden kann -> loggen, aber weitermachen
			return null;
		}
	}

	private List<Paarung> lade(String league, String season) {
		try {
			return spieltagRepo.lade(league, season);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
