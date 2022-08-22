package de.larmic.ddd.application.person

import de.larmic.ddd.common.UseCase
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository

@UseCase
class PersonHinzufuegen(private val personRepository: PersonRepository) {

    // TODO prüfen, ob eine Person mit dem LDAP bereits existiert
    fun fuegePersonHinzu(person: Person) = if (personRepository existiert person.id) {
        PersonExistiertBereits
    } else {
        personRepository.legeAn(person)
        Ok(person)
    }

    sealed class Result

    class Ok(val person: Person) : Result()
    object PersonExistiertBereits : Result()
}