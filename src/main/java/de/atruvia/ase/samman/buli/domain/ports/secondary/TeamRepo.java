package de.atruvia.ase.samman.buli.domain.ports.secondary;

import java.util.List;

import de.atruvia.ase.samman.buli.domain.Team;

public interface TeamRepo {

	List<Team> getTeams(String league, String season) throws Exception;

}
