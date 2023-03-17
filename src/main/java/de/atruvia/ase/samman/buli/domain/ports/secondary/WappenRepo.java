package de.atruvia.ase.samman.buli.domain.ports.secondary;

import java.net.URI;

public interface WappenRepo {

	URI getWappen(String league, String season, String team) throws Exception;

}
