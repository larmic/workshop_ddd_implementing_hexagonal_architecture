package de.larmic.ddd.application.raum

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.raum.Raum
import de.larmic.ddd.domain.raum.RaumRepository

@UseCase
class RaumHinzufuegen(private val raumRepository: RaumRepository) {

    operator fun invoke(raum: Raum) = if (raumRepository existiert raum.nummer) {
        RaumExistiertBereits
    } else {
        raumRepository.legeAn(raum)
        Ok(raum)
    }

    sealed class Result

    class Ok(val raum: Raum) : Result()
    object RaumExistiertBereits : Result()
}