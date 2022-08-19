package de.larmic.ddd.domain

import de.larmic.ddd.common.Repository

@Repository
interface RaumRepository {

    fun legeAn(raum: Raum)
    fun aktualisiere(raum: Raum)
    fun finde(nummer: Raum.Nummer): Raum?
    fun finde(personLdap: Person.Ldap) : Raum?
    fun existiert(nummer: Raum.Nummer): Boolean

}