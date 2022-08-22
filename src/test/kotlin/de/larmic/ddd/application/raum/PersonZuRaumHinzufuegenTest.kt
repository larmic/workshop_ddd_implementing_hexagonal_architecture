package de.larmic.ddd.application.raum

import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.createPersonTestData
import de.larmic.ddd.domain.raum.Raum
import de.larmic.ddd.domain.raum.RaumRepository
import de.larmic.ddd.domain.raum.createRaumTestData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Add person to room with")
internal class PersonZuRaumHinzufuegenTest {

    private val raumRepositoryMock = mockk<RaumRepository>(relaxed = true)
    private val personZuRaumHinzufuegen = PersonZuRaumHinzufuegen(raumRepositoryMock)

    @Test
    internal fun `room exists and person is not part of it`() {
        val raum = createRaumTestData()
        val person = createPersonTestData()

        every { raumRepositoryMock.finde(raum.id) } returns raum
        every { raumRepositoryMock.finde(any<Person.Ldap>()) } returns null

        val result = personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(raum.id, person) as PersonZuRaumHinzufuegen.Ok

        assertThat(result.person.vorname).isEqualTo(person.vorname)
        assertThat(result.person.nachname).isEqualTo(person.nachname)
        assertThat(result.person.ldap).isEqualTo(person.ldap)
        assertThat(result.person.titel).isEqualTo(person.titel)
        assertThat(result.person.namenszusatz).isEqualTo(person.namenszusatz)
        assertThat(result.person.kurzschreibweise).isEqualTo(person.kurzschreibweise)

        verify {
            raumRepositoryMock.aktualisiere(withArg {
                assertThat(it.nummer).isEqualTo(raum.nummer)
                assertThat(it.name.value).isEqualTo(raum.name.value)
                assertThat(it.personen).containsExactly(person.kurzschreibweise)
            })
        }
    }

    @Test
    internal fun `room exists and person is already part of it`() {
        val person = createPersonTestData()
        val raum = createRaumTestData(persons = mutableListOf(person))

        every { raumRepositoryMock.finde(raum.id) } returns raum
        every { raumRepositoryMock.finde(any<Person.Ldap>()) } returns null

        val answer = personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(raum.id, person)

        assertThat(answer).isEqualTo(PersonZuRaumHinzufuegen.PersonIstDemRaumBereitsZugewiesen)

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

        val answer = personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(raum1.id, person)

        assertThat(answer).isEqualTo(PersonZuRaumHinzufuegen.PersonIstEinemAnderenRaumBereitsZugewiesen)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }

    @Test
    internal fun `room not exists`() {
        val person = createPersonTestData()
        val roomId = Raum.Id()

        every { raumRepositoryMock.finde(roomId) } returns null

        val answer = personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(roomId, person)

        assertThat(answer).isEqualTo(PersonZuRaumHinzufuegen.RaumNichtGefunden)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }
}