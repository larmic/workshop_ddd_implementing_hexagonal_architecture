package de.larmic.ddd.infrastructure.person.database

import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository
import org.springframework.stereotype.Repository

@Repository
class PersonRepositoryImpl : PersonRepository {

    override fun legeAn(person: Person) {
        TODO("Not yet implemented")
    }

    override fun finde(id: Person.Id): Person? {
        TODO("Not yet implemented")
    }
}