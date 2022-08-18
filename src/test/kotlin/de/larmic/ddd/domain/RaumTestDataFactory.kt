package de.neusta.larmic.ddd.domain

fun createRaumTestData(
    raumNummer: String = "0815",
    raumName: String = "Ein kleiner Raum"
) = Raum(nummer = Raum.Nummer(value = raumNummer), name = Raum.Name(value = raumName))
