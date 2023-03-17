package de.atruvia.ase.samman.buli.domain.ports.secondary;

import java.net.URI;

public interface TeamRepo {

	URI getTeam(String league, String season, String team) throws Exception;

}
