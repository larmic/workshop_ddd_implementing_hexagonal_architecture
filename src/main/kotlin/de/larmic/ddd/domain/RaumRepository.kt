package de.larmic.ddd.domain

import de.larmic.ddd.common.Repository

@Repository
interface RaumRepository {

    fun legeAn(raum: Raum)
    fun finde(nummer: Raum.Nummer): Raum?
    fun existiert(nummer: Raum.Nummer): Boolean

}