# language: de

Funktionalität: Tabelle erstellen

Szenario: Zwei Mannschaften sind punkt- und torgleich

	Gegeben sei ein Spielplan
		| Heim   | Gast   | Ergebnis |
		| Team 1 | Team 2 |      1:0 |
		| Team 2 | Team 1 |      1:0 |
		| Team 1 | Team 3 |      1:0 |
		| Team 2 | Team 3 |      1:0 |
	Wenn die Tabelle berechnet wird
	Dann ist die Tabelle
		| Platz|Team  |Spiele|Siege|Unentschieden|Niederlagen|Punkte|Tore|Gegentore|Tordifferenz|
		| 1    |Team 1|3     |    2|            0|          1|     6|   2|        1|           1|
		| 1    |Team 2|3     |    2|            0|          1|     6|   2|        1|           1|
		| 3    |Team 3|2     |    0|            0|          2|     0|   0|        2|          -2|
