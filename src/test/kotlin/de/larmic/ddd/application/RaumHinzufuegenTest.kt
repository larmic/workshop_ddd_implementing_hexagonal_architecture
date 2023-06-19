package de.larmic.ddd.application

import de.larmic.ddd.domain.RaumRepository
import de.larmic.ddd.domain.createRaumTestData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RaumHinzufuegenTest {

    private val raumRepositoryMock = mockk<RaumRepository>(relaxed = true)
    private val raumHinzufuegen = RaumHinzufuegen(raumRepositoryMock)

    @Test
    internal fun `add a not existing room`() {
        val raum = createRaumTestData()

        every { raumRepositoryMock.existiert(raum.nummer) } returns false

        val result = raumHinzufuegen(raum) as RaumHinzufuegen.Ok

        assertThat(raum.id).isEqualTo(result.raum.id)
        assertThat(raum.nummer.value).isEqualTo(result.raum.nummer.value)
        assertThat(raum.name.value).isEqualTo(result.raum.name.value)

        verify {
            raumRepositoryMock.legeAn(withArg {
                assertThat(it.nummer.value).isEqualTo(raum.nummer.value)
                assertThat(it.name.value).isEqualTo(raum.name.value)
            })
        }
    }

    @Test
    internal fun `add existing room`() {
        val raum = createRaumTestData()

        every { raumRepositoryMock.existiert(raum.nummer) } returns true

        val result = raumHinzufuegen(raum)

        assertThat(result).isEqualTo(RaumHinzufuegen.RaumExistiertBereits)

        verify(exactly = 0) { raumRepositoryMock.legeAn(any()) }
    }
}