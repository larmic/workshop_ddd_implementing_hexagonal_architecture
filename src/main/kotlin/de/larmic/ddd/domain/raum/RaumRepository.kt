package de.larmic.ddd.domain.raum

import de.larmic.ddd.common.Repository

@Repository
interface RaumRepository {

    fun legeAn(raum: Raum)
    fun aktualisiere(raum: Raum)
    fun finde(id: Raum.Id): Raum?
    infix fun existiert(nummer: Raum.Nummer): Boolean

}