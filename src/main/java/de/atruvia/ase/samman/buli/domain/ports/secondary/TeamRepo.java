package de.atruvia.ase.samman.buli.domain.ports.secondary;

import java.net.URI;

public interface TeamRepo {

	URI getTeams(String league, String season, String team) throws Exception;

	URI getTeams(String league, String season) throws Exception;

}
