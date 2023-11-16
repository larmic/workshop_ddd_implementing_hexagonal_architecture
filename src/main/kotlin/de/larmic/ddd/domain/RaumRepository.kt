package de.larmic.ddd.domain

import de.larmic.ddd.common.Repository

@Repository
interface RaumRepository {

    fun legeAn(raum: Raum)
    fun aktualisiere(raum: Raum)
    fun finde(id: Raum.Id): Raum?
    fun finde(benutzername: Person.Benutzername) : Raum?
    infix fun existiert(nummer: Raum.Nummer): Boolean

}