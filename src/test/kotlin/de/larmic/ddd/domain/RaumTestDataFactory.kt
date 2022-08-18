package de.larmic.ddd.domain

import de.larmic.ddd.domain.Raum

fun createRaumTestData(
    raumNummer: String = "0815",
    raumName: String = "Ein kleiner Raum"
) = Raum(nummer = Raum.Nummer(value = raumNummer), name = Raum.Name(value = raumName))
