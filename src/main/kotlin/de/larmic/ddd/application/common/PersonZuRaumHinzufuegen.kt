package de.larmic.ddd.application.common

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository
import de.larmic.ddd.domain.raum.EventRepository
import de.larmic.ddd.domain.raum.PersonWurdeRaumZugeordnetEvent
import de.larmic.ddd.domain.raum.Raum
import de.larmic.ddd.domain.raum.RaumRepository

// Befindet sich im common-Package, weil zwei Aggregate abgerufen werden.
@UseCase
class PersonZuRaumHinzufuegen(
    private val raumRepository: RaumRepository,
    private val personRepository: PersonRepository,
    private val eventRepository: EventRepository,
) {

    operator fun invoke(raumId: Raum.Id, personId: Person.Id): Result {
        val raum = raumRepository.finde(raumId) ?: return RaumNichtGefunden
        val person = personRepository.finde(personId) ?: return PersonNichtGefunden

        try {
            raum.fuegeHinzu(person.id)
        } catch (e: IllegalArgumentException) {
            return PersonIstDemRaumBereitsZugewiesen
        }

        raumRepository.aktualisiere(raum)

        eventRepository.sende(PersonWurdeRaumZugeordnetEvent(raumId = raumId, personId = person.id))

        return Ok
    }

    sealed class Result

    object Ok : Result()
    object PersonIstDemRaumBereitsZugewiesen : Result()
    object RaumNichtGefunden : Result()
    object PersonNichtGefunden : Result()
}