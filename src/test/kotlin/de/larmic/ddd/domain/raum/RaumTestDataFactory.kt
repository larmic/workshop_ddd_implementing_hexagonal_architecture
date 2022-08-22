package de.larmic.ddd.domain.raum

import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.raum.Raum
import java.util.*

fun createRaumTestData(
    id: UUID = UUID.randomUUID(),
    raumNummer: String = "0815",
    raumName: String = "Ein kleiner Raum",
    persons: MutableList<Person> = mutableListOf()
) = Raum(id = Raum.Id(value = id), nummer = Raum.Nummer(value = raumNummer), name = Raum.Name(value = raumName), persons = persons)
