package de.larmic.ddd.infrastructure.rest

import de.larmic.ddd.application.Ok
import de.larmic.ddd.application.RaumExistiertBereits
import de.larmic.ddd.application.RaumHinzufuegen
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class RoomRestController(private val raumRepository: RaumRepository, private val raumHinzufuegen: RaumHinzufuegen) {

    @PostMapping(value = ["/api/room"], consumes = ["application/json"], produces = ["application/json"])
    fun postRoom(@RequestBody dto: CreateRoomDto): ResponseEntity<Any> {
        val answer = raumHinzufuegen.fuegeRaumHinzu(dto.mapToDomain())

        return when (answer) {
            Ok -> ResponseEntity.ok().build()
            RaumExistiertBereits -> ResponseEntity.badRequest().body("Room number ${dto.number} already exists")
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

    private fun Raum.mapToDto() = RoomDto(number = this.nummer.value, name = this.name.value)
}

class RoomDto(val number: String, val name: String)
class CreateRoomDto(val number: String, val name: String)
