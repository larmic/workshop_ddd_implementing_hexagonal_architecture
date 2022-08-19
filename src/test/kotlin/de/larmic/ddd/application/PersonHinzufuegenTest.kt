package de.larmic.ddd.application

import de.larmic.ddd.domain.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Add person to room with")
internal class PersonHinzufuegenTest {

    private val raumRepositoryMock = mockk<RaumRepository>(relaxed = true)
    private val personHinzufuegen = PersonHinzufuegen(raumRepositoryMock)

    @Test
    internal fun `room exists and person is not part of it`() {
        val raum = createRaumTestData()
        val person = createPersonTestData()

        every { raumRepositoryMock.finde(raum.nummer) } returns raum
        every { raumRepositoryMock.finde(any<Person.Ldap>()) } returns null

        val answer = personHinzufuegen.fuegePersonZuRaumHinzu(raum.nummer, person)

        assertThat(answer).isEqualTo(PersonHinzufuegen.Ok)

        verify {
            raumRepositoryMock.aktualisiere(withArg {
                assertThat(it.nummer).isEqualTo(raum.nummer)
                assertThat(it.name.value).isEqualTo(raum.name.value)
                assertThat(it.personen).containsExactly(person.fullName)
            })
        }
    }

    @Test
    internal fun `room exists and person is already part of it`() {
        val person = createPersonTestData()
        val raum = createRaumTestData(persons = mutableListOf(person))

        every { raumRepositoryMock.finde(raum.nummer) } returns raum
        every { raumRepositoryMock.finde(any<Person.Ldap>()) } returns null

        val answer = personHinzufuegen.fuegePersonZuRaumHinzu(raum.nummer, person)

        assertThat(answer).isEqualTo(PersonHinzufuegen.PersonIstDemRaumBereitsZugewiesen)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }

    @Test
    internal fun `room exists and person is already part of another room`() {
        val person = createPersonTestData()
        val raum1 = createRaumTestData()
        val raum2 = createRaumTestData(raumNummer = "0987", raumName = "anderer raum", persons = mutableListOf(person))

        every { raumRepositoryMock.finde(raum1.nummer) } returns raum1
        every { raumRepositoryMock.finde(raum2.nummer) } returns raum2
        every { raumRepositoryMock.finde(person.ldap) } returns raum2

        val answer = personHinzufuegen.fuegePersonZuRaumHinzu(raum1.nummer, person)

        assertThat(answer).isEqualTo(PersonHinzufuegen.PersonIstEinemAnderenRaumBereitsZugewiesen)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }

    @Test
    internal fun `room not exists`() {
        val person = createPersonTestData()
        val nummer = Raum.Nummer("0815")

        every { raumRepositoryMock.finde(nummer) } returns null

        val answer = personHinzufuegen.fuegePersonZuRaumHinzu(nummer, person)

        assertThat(answer).isEqualTo(PersonHinzufuegen.RaumNichtGefunden)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }
}