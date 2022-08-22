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

    // TODO, wenn statt Person.Id Raum.PersonRefId verwendet wird, kann der Usecase zurück in das raum-package.
    fun fuegePersonZuRaumHinzu(raumId: Raum.Id, personId: Person.Id): Result {
        val raum = raumRepository.finde(raumId) ?: return RaumNichtGefunden
        val person = personRepository.finde(personId) ?: return PersonNichtGefunden

        try {
            raum.fuegeHinzu(person.id.mapToPersonRefId())
        } catch (e: IllegalArgumentException) {
            return PersonIstDemRaumBereitsZugewiesen
        }

        raumRepository.aktualisiere(raum)

        return Ok
    }

    sealed class Result

    object Ok : Result()
    object PersonIstDemRaumBereitsZugewiesen : Result()
    object RaumNichtGefunden : Result()
    object PersonNichtGefunden : Result()
}

private fun Person.Id.mapToPersonRefId() = Raum.PersonRefId(value = this.value)