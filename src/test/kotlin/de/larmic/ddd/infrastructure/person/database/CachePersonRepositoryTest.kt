package de.larmic.ddd.infrastructure.person.database

import de.larmic.ddd.domain.person.createPersonTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CachePersonRepositoryTest {

    private val personRepository = CachePersonRepository()

    @Test
    internal fun `store and get a person`() {
        val person = createPersonTestData()

        personRepository.legeAn(person)

        assertThat(personRepository.size).isEqualTo(1)
        assertThat(personRepository.finde(person.id)).isNotNull
        assertThat(personRepository.finde(person.id)!!.vorname.value).isEqualTo(person.vorname.value)
        assertThat(personRepository.finde(person.id)!!.nachname.value).isEqualTo(person.nachname.value)
        assertThat(personRepository.finde(person.id)!!.ldap.value).isEqualTo(person.ldap.value)
        assertThat(personRepository.finde(person.id)!!.titel?.value).isEqualTo(person.titel?.value)
        assertThat(personRepository.finde(person.id)!!.namenszusatz?.value).isEqualTo(person.namenszusatz?.value)
        assertThat(personRepository.finde(person.id)!!.kurzschreibweise).isEqualTo(person.kurzschreibweise)
    }

    @Test
    internal fun `store a person twice`() {
        val person = createPersonTestData()

        personRepository.legeAn(person)
        personRepository.legeAn(person)

        assertThat(personRepository.size).isEqualTo(1)
        assertThat(personRepository.finde(person.id)!!.vorname.value).isEqualTo(person.vorname.value)
        assertThat(personRepository.finde(person.id)!!.nachname.value).isEqualTo(person.nachname.value)
        assertThat(personRepository.finde(person.id)!!.ldap.value).isEqualTo(person.ldap.value)
        assertThat(personRepository.finde(person.id)!!.titel?.value).isEqualTo(person.titel?.value)
        assertThat(personRepository.finde(person.id)!!.namenszusatz?.value).isEqualTo(person.namenszusatz?.value)
        assertThat(personRepository.finde(person.id)!!.kurzschreibweise).isEqualTo(person.kurzschreibweise)
    }
}