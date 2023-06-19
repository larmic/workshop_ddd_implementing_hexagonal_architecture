package de.larmic.ddd.domain.raum

import de.larmic.ddd.domain.person.Person
import java.util.*

fun createRaumTestData(
    id: UUID = UUID.randomUUID(),
    raumNummer: String = "0815",
    raumName: String = "Ein kleiner Raum",
    persons: MutableList<Person.Id> = mutableListOf()
) = Raum(id = Raum.Id(value = id), nummer = Raum.Nummer(value = raumNummer), name = Raum.Name(value = raumName), personIds = persons)
