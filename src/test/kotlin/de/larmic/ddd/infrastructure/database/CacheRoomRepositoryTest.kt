package de.larmic.ddd.infrastructure.database

import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.createRaumTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CacheRoomRepositoryTest {

    private val roomRepository = CacheRoomRepository()

    @Test
    internal fun `store and get a room`() {
        val raum = createRaumTestData()

        roomRepository.speichere(raum)

        assertThat(roomRepository.size).isEqualTo(1)
        assertThat(roomRepository.finde(raum.nummer)).isNotNull
        assertThat(roomRepository.finde(raum.nummer)!!.nummer.value).isEqualTo(raum.nummer.value)
        assertThat(roomRepository.finde(raum.nummer)!!.name.value).isEqualTo(raum.name.value)
    }

    @Test
    internal fun `store a room twice`() {
        val raum = createRaumTestData()

        roomRepository.speichere(raum)
        roomRepository.speichere(raum)

        assertThat(roomRepository.size).isEqualTo(1)
        assertThat(roomRepository.finde(raum.nummer)).isNotNull
        assertThat(roomRepository.finde(raum.nummer)!!.nummer.value).isEqualTo(raum.nummer.value)
        assertThat(roomRepository.finde(raum.nummer)!!.name.value).isEqualTo(raum.name.value)
    }

    @Test
    internal fun `not containing room`() {
        assertThat(roomRepository.existiert(Raum.Nummer(value = "4711"))).isFalse
    }

    @Test
    internal fun `containing room`() {
        val raum = createRaumTestData()

        roomRepository.speichere(raum)

        assertThat(roomRepository.existiert(raum.nummer)).isTrue
    }
}