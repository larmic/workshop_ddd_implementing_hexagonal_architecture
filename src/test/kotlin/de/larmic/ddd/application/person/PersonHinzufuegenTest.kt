package de.larmic.ddd.application.person

import de.larmic.ddd.domain.person.PersonRepository
import de.larmic.ddd.domain.person.createPersonTestData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PersonHinzufuegenTest {

    private val personRepositoryMock = mockk<PersonRepository>(relaxed = true)
    private val personHinzufuegen = PersonHinzufuegen(personRepositoryMock)

    @Test
    internal fun `add a not existing person`() {
        val person = createPersonTestData()

        every { personRepositoryMock.existiert(person.id) } returns false

        val result = personHinzufuegen(person) as PersonHinzufuegen.Ok

        assertThat(person.id).isEqualTo(result.person.id)
        assertThat(person.vorname.value).isEqualTo(result.person.vorname.value)
        assertThat(person.nachname.value).isEqualTo(result.person.nachname.value)
        assertThat(person.benutzername.value).isEqualTo(result.person.benutzername.value)
        assertThat(person.namenszusatz?.value).isEqualTo(result.person.namenszusatz?.value)
        assertThat(person.kurzschreibweise).isEqualTo(result.person.kurzschreibweise)

        verify {
            personRepositoryMock.legeAn(withArg {
                assertThat(it.id).isEqualTo(person.id)
                assertThat(it.vorname.value).isEqualTo(person.vorname.value)
                assertThat(it.nachname.value).isEqualTo(person.nachname.value)
                assertThat(it.benutzername.value).isEqualTo(person.benutzername.value)
                assertThat(it.namenszusatz?.value).isEqualTo(person.namenszusatz?.value)
                assertThat(it.kurzschreibweise).isEqualTo(person.kurzschreibweise)
            })
        }
    }

    @Test
    internal fun `add existing person by id`() {
        val person = createPersonTestData()

        every { personRepositoryMock.existiert(person.id) } returns true
        every { personRepositoryMock.existiert(person.benutzername) } returns false

        val result = personHinzufuegen(person)

        assertThat(result).isEqualTo(PersonHinzufuegen.PersonExistiertBereits)

        verify(exactly = 0) { personRepositoryMock.legeAn(any()) }
    }

    @Test
    internal fun `add existing person by benutzername`() {
        val person = createPersonTestData()

        every { personRepositoryMock.existiert(person.id) } returns false
        every { personRepositoryMock.existiert(person.benutzername) } returns true

        val result = personHinzufuegen(person)

        assertThat(result).isEqualTo(PersonHinzufuegen.PersonExistiertBereits)

        verify(exactly = 0) { personRepositoryMock.legeAn(any()) }
    }
}