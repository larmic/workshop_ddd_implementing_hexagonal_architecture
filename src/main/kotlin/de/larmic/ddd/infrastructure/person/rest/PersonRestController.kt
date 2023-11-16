package de.larmic.ddd.infrastructure.person.rest

import de.larmic.ddd.application.person.PersonHinzufuegen
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class PersonRestController(
    private val personRepository: PersonRepository,
    private val personHinzufuegen: PersonHinzufuegen,
) {

    @PostMapping(value = ["/api/person"], consumes = ["application/json"], produces = ["application/json"])
    fun postPerson(@RequestBody dto: CreatePersonDto): ResponseEntity<Any> {
        return when (val result = personHinzufuegen(dto.mapToDomain())) {
            is PersonHinzufuegen.Ok -> ResponseEntity.ok(result.person.mapToReadDto())
            PersonHinzufuegen.PersonExistiertBereits -> ResponseEntity.badRequest().build()
        }
    }

    @GetMapping(value = ["/api/person/{id}"])
    fun getRoom(@PathVariable id: String): ResponseEntity<Any> {
        val person = personRepository.finde(Person.Id(UUID.fromString(id))) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(person.mapToReadDto())
    }
}

class ReadPersonDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val benutzername: String,
    val addition: String?,
)

class CreatePersonDto(
    val firstName: String,
    val lastName: String,
    val userName: String,
    val addition: String?,
)

private fun Person.mapToReadDto() = ReadPersonDto(
    id = this.id.value.toString(),
    firstName = this.vorname.value,
    lastName = this.nachname.value,
    benutzername = this.benutzername.value,
    addition = this.namenszusatz?.value,
)

private fun CreatePersonDto.mapToDomain() = Person(
    vorname = Person.Vorname(this.firstName),
    nachname = Person.Nachname(this.lastName),
    benutzername = Person.Benutzername(this.userName),
    namenszusatz = Person.Namenszusatz.create(this.addition),
)