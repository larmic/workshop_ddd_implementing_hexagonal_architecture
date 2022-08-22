package de.larmic.ddd.domain.raum

import de.larmic.ddd.common.DomainEvent

@DomainEvent
data class PersonWurdeRaumZugeordnetEvent(val raumId: Raum.Id, val personRefId: Raum.PersonRefId)