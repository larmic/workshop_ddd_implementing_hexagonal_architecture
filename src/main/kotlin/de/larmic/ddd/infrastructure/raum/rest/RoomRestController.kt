package de.larmic.ddd.infrastructure.rest

import de.larmic.ddd.application.common.PersonZuRaumHinzufuegen
import de.larmic.ddd.application.common.RaumLaden
import de.larmic.ddd.application.raum.RaumHinzufuegen
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.raum.Raum
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class RoomRestController(
    private val raumHinzufuegen: RaumHinzufuegen,
    private val personZuRaumHinzufuegen: PersonZuRaumHinzufuegen,
    private val raumLaden: RaumLaden,
) {

    @PostMapping(value = ["/api/room"], consumes = ["application/json"], produces = ["application/json"])
    fun postRoom(@RequestBody dto: CreateRoomDto): ResponseEntity<Any> {
        return when (val result = raumHinzufuegen(dto.mapToDomain())) {
            is RaumHinzufuegen.Ok -> ResponseEntity.ok(result.raum.mapToDto())
            RaumHinzufuegen.RaumExistiertBereits -> ResponseEntity.badRequest().body("Room number ${dto.number} already exists")
        }
    }

    @PutMapping(value = ["/api/room/{raumId}/person/{personId}"])
    fun postPerson(@PathVariable raumId: String, @PathVariable personId: String): ResponseEntity<Any> {
        return when (personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(Raum.Id(UUID.fromString(raumId)), Person.Id(UUID.fromString(personId)))) {
            is PersonZuRaumHinzufuegen.Ok -> ResponseEntity.ok().build()
            PersonZuRaumHinzufuegen.PersonIstDemRaumBereitsZugewiesen -> ResponseEntity.badRequest().build()
            PersonZuRaumHinzufuegen.RaumNichtGefunden -> ResponseEntity.badRequest().build()
            PersonZuRaumHinzufuegen.PersonNichtGefunden -> ResponseEntity.badRequest().build()
        }
    }

    @GetMapping(value = ["/api/room/{id}"])
    fun getRoom(@PathVariable id: String): ResponseEntity<Any> {
        return when(val result = raumLaden.lade(Raum.Id(UUID.fromString(id)))) {
            is RaumLaden.Ok -> ResponseEntity.ok(result.raumMitPersonen.mapToDto())
            RaumLaden.RaumNichtGefunden -> ResponseEntity.notFound().build()
        }
    }
}

class CreateRoomDto(val number: String, val name: String)
class ReadRoomDto(val id: String, val number: String, val name: String, val persons: List<String>)
class CreatePersonDto(
    val firstName: String,
    val lastName: String,
    val ldap: String,
    val title: String?,
    val addition: String?,
)

private fun CreateRoomDto.mapToDomain() = Raum(
    nummer = Raum.Nummer(value = this.number),
    name = Raum.Name(value = this.name)
)

private fun Raum.mapToDto() =
    ReadRoomDto(
        id = this.id.value.toString(),
        number = this.nummer.value,
        name = this.name.value,
        persons = emptyList(),
    )

private fun RaumLaden.RaumMitPersonen.mapToDto() =
    ReadRoomDto(
        id = this.raum.id.value.toString(),
        number = this.raum.nummer.value,
        name = this.raum.name.value,
        persons = this.persons.map { it.kurzschreibweise },
    )