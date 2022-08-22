package de.larmic.ddd.infrastructure.rest

import de.larmic.ddd.application.raum.PersonZuRaumHinzufuegen
import de.larmic.ddd.application.raum.RaumHinzufuegen
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.raum.Raum
import de.larmic.ddd.domain.raum.RaumRepository
import de.larmic.ddd.infrastructure.person.rest.CreatePersonDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class RoomRestController(
    private val raumRepository: RaumRepository,
    private val raumHinzufuegen: RaumHinzufuegen,
    private val personZuRaumHinzufuegen: PersonZuRaumHinzufuegen,
) {

    @PostMapping(value = ["/api/room"], consumes = ["application/json"], produces = ["application/json"])
    fun postRoom(@RequestBody dto: CreateRoomDto): ResponseEntity<Any> {
        return when (val result = raumHinzufuegen.fuegeRaumHinzu(dto.mapToDomain())) {
            is RaumHinzufuegen.Ok -> ResponseEntity.ok(result.raum.mapToDto())
            RaumHinzufuegen.RaumExistiertBereits -> ResponseEntity.badRequest().body("Room number ${dto.number} already exists")
        }
    }

    @PutMapping(value = ["/api/room/{id}/person"], consumes = ["application/json"], produces = ["application/json"])
    fun postPerson(@PathVariable id: String, @RequestBody dto: CreatePersonDto): ResponseEntity<Any> {
        return when (personZuRaumHinzufuegen.fuegePersonZuRaumHinzu(Raum.Id(UUID.fromString(id)), dto.mapToDomain())) {
            is PersonZuRaumHinzufuegen.Ok -> ResponseEntity.ok().build()
            PersonZuRaumHinzufuegen.PersonIstDemRaumBereitsZugewiesen -> ResponseEntity.badRequest().build()
            PersonZuRaumHinzufuegen.RaumNichtGefunden -> ResponseEntity.badRequest().build()
            PersonZuRaumHinzufuegen.PersonIstEinemAnderenRaumBereitsZugewiesen -> ResponseEntity.badRequest().build()
        }
    }

    @GetMapping(value = ["/api/room/{id}"])
    fun getRoom(@PathVariable id: String): ResponseEntity<Any> {
        val raum = raumRepository.finde(Raum.Id(UUID.fromString(id))) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(raum.mapToDto())
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
        persons = this.personen,
    )

private fun CreatePersonDto.mapToDomain() = Person(
    vorname = Person.Vorname(this.firstName),
    nachname = Person.Nachname(this.lastName),
    ldap = Person.Ldap(this.ldap),
    titel = Person.Titel.create(this.title),
    namenszusatz = Person.Namenszusatz.create(this.addition),
)