package de.larmic.ddd.domain

import java.util.*

fun createRaumTestData(
    id: UUID = UUID.randomUUID(),
    raumNummer: String = "0815",
    raumName: String = "Ein kleiner Raum",
    persons: MutableList<Person> = mutableListOf()
) = Raum(id = Raum.Id(value = id), nummer = Raum.Nummer(value = raumNummer), name = Raum.Name(value = raumName), personen = persons)
