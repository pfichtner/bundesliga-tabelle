package de.atruvia.ase.samman.buli.domain;

import java.net.URI;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Team {
	String name;
	URI wappen;
}
