package de.larmic.ddd.domain.raum

import java.util.*

fun createPersonWurdeRaumZugeordnetEventTestData(
    raumId: UUID = UUID.randomUUID(),
    personRefId: UUID = UUID.randomUUID(),
) = PersonWurdeRaumZugeordnetEvent(raumId = Raum.Id(raumId), personRefId = Raum.PersonRefId(personRefId))