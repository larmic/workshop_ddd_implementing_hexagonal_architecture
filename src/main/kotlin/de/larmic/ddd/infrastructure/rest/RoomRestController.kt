package de.larmic.ddd.infrastructure.rest

import de.larmic.ddd.application.Ok
import de.larmic.ddd.application.PersonHinzufuegen
import de.larmic.ddd.application.RaumExistiertBereits
import de.larmic.ddd.application.RaumHinzufuegen
import de.larmic.ddd.domain.Person
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class RoomRestController(
    private val raumRepository: RaumRepository,
    private val raumHinzufuegen: RaumHinzufuegen,
    private val personHinzufuegen: PersonHinzufuegen,
) {

    @PostMapping(value = ["/api/room"], consumes = ["application/json"], produces = ["application/json"])
    fun postRoom(@RequestBody dto: CreateRoomDto): ResponseEntity<Any> {
        val answer = raumHinzufuegen.fuegeRaumHinzu(dto.mapToDomain())

        return when (answer) {
            Ok -> ResponseEntity.ok().build()
            RaumExistiertBereits -> ResponseEntity.badRequest().body("Room number ${dto.number} already exists")
        }
    }

    @PutMapping(value = ["/api/room/{number}/person"], consumes = ["application/json"], produces = ["application/json"])
    fun postPerson(@PathVariable number: String, @RequestBody dto: CreatePersonDto): ResponseEntity<Any> {
        return when (personHinzufuegen.fuegePersonZuRaumHinzu(Raum.Nummer(number), dto.mapToDomain())) {
            PersonHinzufuegen.Ok -> ResponseEntity.ok().build()
            PersonHinzufuegen.PersonIstDemRaumBereitsZugewiesen -> ResponseEntity.badRequest().build()
            PersonHinzufuegen.RaumNichtGefunden -> ResponseEntity.badRequest().build()
            PersonHinzufuegen.PersonIstEinemAnderenRaumBereitsZugewiesen -> ResponseEntity.badRequest().build()
        }
    }

    @GetMapping(value = ["/api/room/{number}"])
    fun getRoom(@PathVariable number: String): ResponseEntity<Any> {
        val raum = raumRepository.finde(Raum.Nummer(number)) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(raum.mapToDto())
    }

    private fun CreateRoomDto.mapToDomain() = Raum(
        nummer = Raum.Nummer(value = this.number),
        name = Raum.Name(value = this.name)
    )

    private fun Raum.mapToDto() = RoomDto(number = this.nummer.value, name = this.name.value, persons = this.personen)

    private fun CreatePersonDto.mapToDomain() = Person(
        vorname = Person.Vorname(this.firstName),
        nachname = Person.Nachname(this.lastName),
        ldap = Person.Ldap(this.ldap),
        titel = Person.Titel.create(this.title),
        namenszusatz = Person.Namenszusatz.create(this.addition),
    )
}

class RoomDto(val number: String, val name: String, val persons: List<String>)
class CreateRoomDto(val number: String, val name: String)
class CreatePersonDto(
    val firstName: String,
    val lastName: String,
    val ldap: String,
    val title: String?,
    val addition: String?,
)