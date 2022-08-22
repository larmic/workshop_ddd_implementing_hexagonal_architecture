package de.larmic.ddd.domain.person

import de.larmic.ddd.common.Repository

@Repository
interface PersonRepository {

    fun legeAn(person: Person)
    fun finde(id: Person.Id): Person?
    infix fun existiert(id: Person.Id): Boolean

}