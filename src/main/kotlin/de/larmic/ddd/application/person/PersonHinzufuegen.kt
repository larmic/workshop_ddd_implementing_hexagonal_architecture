package de.larmic.ddd.application.person

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository

@UseCase
class PersonHinzufuegen(private val personRepository: PersonRepository) {

    operator fun invoke(person: Person) = if (personRepository beinhaltet person) {
        PersonExistiertBereits
    } else {
        personRepository.legeAn(person)
        Ok(person)
    }

    sealed class Result

    class Ok(val person: Person) : Result()
    object PersonExistiertBereits : Result()
}

private infix fun PersonRepository.beinhaltet(person: Person) = this existiert person.id || this existiert person.benutzername