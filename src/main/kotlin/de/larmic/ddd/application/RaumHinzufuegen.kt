package de.larmic.ddd.application

import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository
import org.springframework.stereotype.Component

@Component
class RaumHinzufuegen(private val raumRepository: RaumRepository) {

    fun fuegeRaumHinzu(raum: Raum) = if (raumRepository.existiert(raum.nummer)) {
        RaumExistiertBereits
    } else {
        raumRepository.speichere(raum)
        Ok
    }

}

sealed class Answer

object Ok : Answer()
object RaumExistiertBereits : Answer()