package de.larmic.ddd.domain

import de.larmic.ddd.common.Repository

@Repository
interface RaumRepository {

    fun legeAn(raum: Raum)
    fun finde(id: Raum.Id): Raum?
    fun existiert(nummer: Raum.Nummer): Boolean

}