package de.larmic.ddd.infrastructure.database

import de.larmic.ddd.domain.Person
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository
import org.springframework.stereotype.Repository

@Repository
class CacheRoomRepository : RaumRepository {

    private val rooms = mutableMapOf<Raum.Nummer, Raum>()

    override fun legeAn(raum: Raum) {
        this.aktualisiere(raum = raum)
    }

    override fun aktualisiere(raum: Raum) {
        rooms[raum.nummer] = raum
    }

    override fun finde(id: Raum.Id) = rooms.filter { it.value.id == id }.map { it.value }.firstOrNull()

    override fun finde(personLdap: Person.Ldap) =
        rooms.values.firstOrNull { it.personenkurzschreibweisen.anyEndsWith("(${personLdap.value})") }

    override fun existiert(nummer: Raum.Nummer) = rooms[nummer] != null

    val size: Int
        get() = rooms.size
}

private fun List<String>.anyEndsWith(value: String) = this.any { it.endsWith(value) }