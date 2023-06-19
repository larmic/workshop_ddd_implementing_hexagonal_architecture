package de.larmic.ddd.domain.raum

import de.larmic.ddd.common.DomainEvent
import de.larmic.ddd.domain.person.Person

@DomainEvent
data class PersonWurdeRaumZugeordnetEvent(val raumId: Raum.Id, val personId: Person.Id)