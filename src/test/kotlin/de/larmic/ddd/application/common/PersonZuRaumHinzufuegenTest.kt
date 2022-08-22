package de.larmic.ddd.application.common

import de.larmic.ddd.domain.person.PersonRepository
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
    private val personRepositoryMock = mockk<PersonRepository>(relaxed = true)
    private val personZuRaumHinzufuegen = PersonZuRaumHinzufuegen(raumRepositoryMock, personRepositoryMock)

    @Test
    internal fun `room exists and person is not part of it`() {
        val raum = createRaumTestData()
        val person = createPersonTestData()

        every { raumRepositoryMock.finde(raum.id) } returns raum
        every { personRepositoryMock.finde(person.id) } returns person

        val result = personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(raum.id, person.id)

        assertThat(result).isEqualTo(PersonZuRaumHinzufuegen.Ok)

        verify {
            raumRepositoryMock.aktualisiere(withArg {
                assertThat(it.nummer).isEqualTo(raum.nummer)
                assertThat(it.name.value).isEqualTo(raum.name.value)
                assertThat(it.personenIds).containsExactly(Raum.PersonRefId(person.id.value))
            })
        }
    }

    @Test
    internal fun `room exists and person is already part of it`() {
        val person = createPersonTestData()
        val raum = createRaumTestData(persons = mutableListOf(Raum.PersonRefId(person.id.value)))

        every { raumRepositoryMock.finde(raum.id) } returns raum
        every { personRepositoryMock.finde(person.id) } returns person

        val result = personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(raum.id, person.id)

        assertThat(result).isEqualTo(PersonZuRaumHinzufuegen.PersonIstDemRaumBereitsZugewiesen)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }

    @Test
    internal fun `room exists and person is already part of another room`() {
        val person = createPersonTestData()
        val raum1 = createRaumTestData()
        val raum2 = createRaumTestData(raumNummer = "0987", persons = mutableListOf(Raum.PersonRefId(person.id.value)))

        every { raumRepositoryMock.finde(raum1.id) } returns raum1
        every { raumRepositoryMock.finde(raum2.id) } returns raum2
        every { personRepositoryMock.finde(person.id) } returns person

        val result = personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(raum1.id, person.id)

        assertThat(result).isEqualTo(PersonZuRaumHinzufuegen.Ok)

        verify {
            raumRepositoryMock.aktualisiere(withArg {
                assertThat(it.nummer.value).isEqualTo(raum1.nummer.value)
                assertThat(it.name.value).isEqualTo(raum1.name.value)
                assertThat(it.personenIds).containsExactly(Raum.PersonRefId(person.id.value))
            })
        }
    }

    @Test
    internal fun `room not exists`() {
        val person = createPersonTestData()
        val roomId = Raum.Id()

        every { raumRepositoryMock.finde(roomId) } returns null
        every { personRepositoryMock.finde(person.id) } returns person

        val result = personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(roomId, person.id)

        assertThat(result).isEqualTo(PersonZuRaumHinzufuegen.RaumNichtGefunden)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }

    @Test
    internal fun `person not exists`() {
        val person = createPersonTestData()
        val raum = createRaumTestData()

        every { raumRepositoryMock.finde(raum.id) } returns raum
        every { personRepositoryMock.finde(person.id) } returns null

        val result = personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(raum.id, person.id)

        assertThat(result).isEqualTo(PersonZuRaumHinzufuegen.PersonNichtGefunden)

        verify(exactly = 0) { raumRepositoryMock.aktualisiere(any()) }
    }
}