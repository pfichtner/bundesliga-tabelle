package de.atruvia.ase.samman.buli.domain;

import java.net.URI;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@Builder
public class Team {
	String name;
	URI wappen;
}
