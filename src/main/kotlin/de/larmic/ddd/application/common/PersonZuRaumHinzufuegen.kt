package de.larmic.ddd.application.common

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository
import de.larmic.ddd.domain.raum.Raum
import de.larmic.ddd.domain.raum.RaumRepository

// Befindet sich im common-Package, weil zwei Aggregate abgerufen werden.
@UseCase
class PersonZuRaumHinzufuegen(
    private val raumRepository: RaumRepository,
    private val personRepository: PersonRepository,
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

        return Ok
    }

    sealed class Result

    data object Ok : Result()
    data object PersonIstDemRaumBereitsZugewiesen : Result()
    data object RaumNichtGefunden : Result()
    data object PersonNichtGefunden : Result()
}