package de.larmic.ddd.application

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository

@UseCase
class RaumHinzufuegen(private val raumRepository: RaumRepository) {

    fun fuegeRaumHinzu(raum: Raum) = if (raumRepository.existiert(raum.nummer)) {
        RaumExistiertBereits
    } else {
        raumRepository.legeAn(raum)
        Ok(raum)
    }

    sealed class Result

    class Ok(val raum: Raum) : Result()
    object RaumExistiertBereits : Result()
}