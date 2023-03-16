package de.atruvia.ase.samman;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class DataRetrieveTest {

	@Test
	void canRetrieveDataOf2022() {
		String url = "https://api.openligadb.de/getmatchdata/bl1/2022";
		List<Paarung> paarungen = lade(url);
		
		Paarung paarung = Paarung.builder().team1("Eintracht Frankfurt").team2("FC Bayern München").tore(1).gegentore(6).build();
		assertThat(paarungen.get(0)).isEqualTo(paarung);

	}

	private List<Paarung> lade(String url) {
		// TODO Auto-generated method stub
		return null;
	}

}
