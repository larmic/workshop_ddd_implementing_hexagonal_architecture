package de.larmic.ddd.domain.raum

import java.util.*

fun createPersonWurdeRaumZugeordnetEventTestData(
    raumId: UUID = UUID.randomUUID(),
    personRefId: UUID = UUID.randomUUID(),
) = PersonWurdeRaumZugeordnetEvent(raumid = Raum.Id(raumId), personRefId = Raum.PersonRefId(personRefId))