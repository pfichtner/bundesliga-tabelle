package de.atruvia.ase.samman;

import java.util.List;

import org.junit.jupiter.api.Test;

class DataRetrieveTest {

	@Test
	void canRetrieveDataOf2022() {
		String url = "https://api.openligadb.de/getmatchdata/bl1/2022";
		List<Paarung> paarungen = lade(url);

	}

	private List<Paarung> lade(String url) {
		// TODO Auto-generated method stub
		return null;
	}

}
