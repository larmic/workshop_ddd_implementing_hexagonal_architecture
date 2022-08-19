package de.larmic.ddd.application

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.Person
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository

@UseCase
class PersonHinzufuegen(val raumRepository: RaumRepository) {

    fun fuegePersonZuRaumHinzu(nummer: Raum.Nummer, person: Person): Answer {
        val raum = raumRepository.finde(nummer) ?: return RaumNichtGefunden

        if (raumRepository.finde(person.ldap) != null) {
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

    sealed class Answer

    object Ok : Answer()
    object PersonIstDemRaumBereitsZugewiesen : Answer()
    object PersonIstEinemAnderenRaumBereitsZugewiesen : Answer()
    object RaumNichtGefunden : Answer()
}