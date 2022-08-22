package de.larmic.ddd.domain.raum

import de.larmic.ddd.common.Repository

@Repository
interface EventRepository {

    fun sende(event: PersonWurdeRaumZugeordnetEvent)

}