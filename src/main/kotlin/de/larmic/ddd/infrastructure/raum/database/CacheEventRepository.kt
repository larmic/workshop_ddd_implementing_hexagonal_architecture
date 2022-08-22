package de.larmic.ddd.infrastructure.raum.database

import de.larmic.ddd.domain.raum.EventRepository
import de.larmic.ddd.domain.raum.PersonWurdeRaumZugeordnetEvent
import org.springframework.stereotype.Repository

/**
 * Repository implementation caches domain events. I.e. a scheduler can read events and push them into a message
 * queue (or post them with http post, or ...). As an alternative this repository can send events directly without
 * persisting them.
 */
@Repository
class CacheEventRepository : EventRepository {

    val events = mutableListOf<PersonWurdeRaumZugeordnetEvent>()

    override fun send(event: PersonWurdeRaumZugeordnetEvent) {
        events.add(event)
    }

    val size: Int
        get() = events.size
}