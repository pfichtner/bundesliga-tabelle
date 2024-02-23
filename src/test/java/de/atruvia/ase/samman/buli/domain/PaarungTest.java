package de.atruvia.ase.samman.buli.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PaarungTest {

	Paarung origin = PaarungMother.paarungWithAllAttributesSet();
	Paarung swappedOnce = origin.swap();

	@Test
	void swapOneTimeIsNotEqualToOrigin() {
		assertThat(swappedOnce).isNotEqualTo(origin);
	}

	@Test
	void swapTwoTimesIsEqualToOrigin() {
		Paarung swappedTwoTimes = swappedOnce.swap();
		assertThat(swappedTwoTimes).isEqualTo(origin);
	}

}
