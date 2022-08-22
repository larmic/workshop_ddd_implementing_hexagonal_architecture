package de.larmic.ddd.infrastructure.person.database

import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository
import org.springframework.stereotype.Repository

@Repository
class CachePersonRepository : PersonRepository {

    private val persons = mutableMapOf<Person.Id, Person>()

    override fun legeAn(person: Person) {
        persons[person.id] = person
    }

    override fun finde(id: Person.Id) = persons[id]

    val size: Int
        get() = persons.size
}