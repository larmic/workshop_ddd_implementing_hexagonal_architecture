package de.larmic.ddd.application.common

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.raum.Raum
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository
import de.larmic.ddd.domain.raum.RaumRepository

// Befindet sich im common-Package, weil zwei Aggregate abgerufen werden.
@UseCase
class RaumLaden(
    private val raumRepository: RaumRepository,
    private val personRepository: PersonRepository,
) {

    operator fun invoke(id: Raum.Id) : Result {
        val raum = raumRepository.finde(id) ?: return RaumNichtGefunden

        return Ok(RaumMitPersonen(
            raum = raum,
            persons = personRepository.ladePersonenDesRaums(raum)
        ))
    }

    // Wrapper verbindet beide Aggregate miteinander
    class RaumMitPersonen(val raum: Raum, val persons: List<Person>)

    sealed class Result

    class Ok(val raumMitPersonen: RaumMitPersonen) : Result()
    data object RaumNichtGefunden : Result()

    private fun PersonRepository.ladePersonenDesRaums(raum: Raum) = raum.personenIds.map { this.finde(id = Person.Id(it.value))!! }
}