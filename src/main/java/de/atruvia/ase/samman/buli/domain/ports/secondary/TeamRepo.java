package de.atruvia.ase.samman.buli.domain.ports.secondary;

import de.atruvia.ase.samman.buli.domain.Team;

public interface TeamRepo {

	Team getTeams(String league, String season) throws Exception;

}
