package de.larmic.ddd.domain.raum

import de.larmic.ddd.common.Repository
import de.larmic.ddd.domain.person.Person

@Repository
interface RaumRepository {

    fun legeAn(raum: Raum)
    fun aktualisiere(raum: Raum)
    fun finde(id: Raum.Id): Raum?
    fun finde(personLdap: Person.Ldap) : Raum?
    infix fun existiert(nummer: Raum.Nummer): Boolean

}