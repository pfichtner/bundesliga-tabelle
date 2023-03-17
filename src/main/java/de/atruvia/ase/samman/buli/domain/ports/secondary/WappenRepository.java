package de.atruvia.ase.samman.buli.domain.ports.secondary;

import java.net.URI;

public interface WappenRepository {

	URI getWappen(String league, String season, String team) throws Exception;

}
