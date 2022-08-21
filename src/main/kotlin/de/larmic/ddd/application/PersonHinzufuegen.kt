package de.larmic.ddd.application

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.Person
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository

@UseCase
class PersonHinzufuegen(val raumRepository: RaumRepository) {

    fun fuegePersonZuRaumHinzu(id: Raum.Id, person: Person): Result {
        val raum = raumRepository.finde(id) ?: return RaumNichtGefunden

        if (raumRepository.finde(person.ldap) != null) {
            return PersonIstEinemAnderenRaumBereitsZugewiesen
        }

        try {
            raum.fuegeHinzu(person)
        } catch (e: IllegalArgumentException) {
            return PersonIstDemRaumBereitsZugewiesen
        }

        raumRepository.aktualisiere(raum)

        return Ok(person)
    }

    sealed class Result

    class Ok(val person: Person) : Result()
    object PersonIstDemRaumBereitsZugewiesen : Result()
    object PersonIstEinemAnderenRaumBereitsZugewiesen : Result()
    object RaumNichtGefunden : Result()
}