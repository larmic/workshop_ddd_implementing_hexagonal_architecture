package de.larmic.ddd.application.person

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository

@UseCase
class PersonHinzufuegen(private val personRepository: PersonRepository) {

    operator fun invoke(person: Person) = if (personRepository existiert person) {
        PersonExistiertBereits
    } else {
        personRepository.legeAn(person)
        Ok(person)
    }

    sealed class Result

    class Ok(val person: Person) : Result()
    data object PersonExistiertBereits : Result()
}

private infix fun PersonRepository.existiert(person: Person) = this existiert person.id || this existiert person.benutzername