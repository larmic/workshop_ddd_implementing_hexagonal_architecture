package de.larmic.ddd.application

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.Person
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository

@UseCase
class PersonZuRaumHinzufuegen(private val raumRepository: RaumRepository) {

     operator fun invoke(id: Raum.Id, person: Person): Result {
        val raum = raumRepository.finde(id) ?: return RaumNichtGefunden

        if (raumRepository.finde(person.benutzername) != null) {
            return PersonIstEinemAnderenRaumBereitsZugewiesen
        }

        try {
            raum.fuegeHinzu(person)
        } catch (e: IllegalArgumentException) {
            return PersonIstDemRaumBereitsZugewiesen
        }

        raumRepository.aktualisiere(raum)

        return Ok
    }

    sealed class Result

    data object Ok : Result()
    data object PersonIstDemRaumBereitsZugewiesen : Result()
    data object PersonIstEinemAnderenRaumBereitsZugewiesen : Result()
    data object RaumNichtGefunden : Result()
}