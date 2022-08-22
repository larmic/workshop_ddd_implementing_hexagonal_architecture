package de.larmic.ddd.infrastructure.raum.database

import de.larmic.ddd.domain.raum.createPersonWurdeRaumZugeordnetEventTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CacheEventRepositoryTest {

    private val eventRepository = CacheEventRepository()

    @Test
    internal fun `send event`() {
        val event = createPersonWurdeRaumZugeordnetEventTestData()

        eventRepository.sende(event)

        assertThat(eventRepository.size).isEqualTo(1)
        assertThat(eventRepository.events).contains(event)
    }

    @Test
    internal fun `send event twice`() {
        val event = createPersonWurdeRaumZugeordnetEventTestData()

        eventRepository.sende(event)
        eventRepository.sende(event)

        assertThat(eventRepository.size).isEqualTo(2)
        assertThat(eventRepository.events).contains(event)
    }
}