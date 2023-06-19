package de.larmic.ddd.domain.raum

import de.larmic.ddd.domain.person.Person
import java.util.*

fun createPersonWurdeRaumZugeordnetEventTestData(
    raumId: UUID = UUID.randomUUID(),
    personId: UUID = UUID.randomUUID(),
) = PersonWurdeRaumZugeordnetEvent(raumId = Raum.Id(raumId), personId = Person.Id(personId))