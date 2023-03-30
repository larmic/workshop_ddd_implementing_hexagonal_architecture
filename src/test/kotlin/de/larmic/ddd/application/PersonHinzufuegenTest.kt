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

        every { raumRepositoryMock.finde(raum.id) } returns raum
        every { raumRepositoryMock.finde(any<Person.Ldap>()) } returns null

        val result = personHinzufuegen.fuegePersonZuRaumHinzu(raum.id, person)

        assertThat(result).isEqualTo(PersonHinzufuegen.Ok)

        verify {
            raumRepositoryMock.aktualisiere(withArg {
                assertThat(it.nummer).isEqualTo(raum.nummer)
                assertThat(it.name.value).isEqualTo(raum.name.value)
                assertThat(it.personenkurzschreibweisen).containsExactly(person.kurzschreibweise)
            })
        }
    }

    @Test
    internal fun `room exists and person is already part of it`() {
        val person = createPersonTestData()
        val raum = createRaumTestData(persons = mutableListOf(person))

        every { raumRepositoryMock.finde(raum.id) } returns raum
        every { raumRepositoryMock.finde(any<Person.Ldap>()) } returns null

        val result = personHinzufuegen.fuegePersonZuRaumHinzu(raum.id, person)

        assertThat(result).isEqualTo(PersonHinzufuegen.PersonIstDemRaumBereitsZugewiesen)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }

    @Test
    internal fun `room exists and person is already part of another room`() {
        val person = createPersonTestData()
        val raum1 = createRaumTestData()
        val raum2 = createRaumTestData(raumNummer = "0987", raumName = "anderer raum", persons = mutableListOf(person))

        every { raumRepositoryMock.finde(raum1.id) } returns raum1
        every { raumRepositoryMock.finde(raum2.id) } returns raum2
        every { raumRepositoryMock.finde(person.ldap) } returns raum2

        val result = personHinzufuegen.fuegePersonZuRaumHinzu(raum1.id, person)

        assertThat(result).isEqualTo(PersonHinzufuegen.PersonIstEinemAnderenRaumBereitsZugewiesen)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }

    @Test
    internal fun `room not exists`() {
        val person = createPersonTestData()
        val roomId = Raum.Id()

        every { raumRepositoryMock.finde(roomId) } returns null

        val result = personHinzufuegen.fuegePersonZuRaumHinzu(roomId, person)

        assertThat(result).isEqualTo(PersonHinzufuegen.RaumNichtGefunden)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }
}