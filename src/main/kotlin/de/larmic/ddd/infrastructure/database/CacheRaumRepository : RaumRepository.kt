package de.larmic.ddd.infrastructure.database

import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository
import org.springframework.stereotype.Repository

@Repository
class CacheRoomRepository : RaumRepository {

    private val rooms = mutableMapOf<Raum.Nummer, Raum>()

    override fun legeAn(raum: Raum) {
        rooms[raum.nummer] = raum
    }

    override fun finde(nummer: Raum.Nummer) = rooms[nummer]

    override fun existiert(nummer: Raum.Nummer) = rooms[nummer] != null

    val size: Int
        get() = rooms.size
}