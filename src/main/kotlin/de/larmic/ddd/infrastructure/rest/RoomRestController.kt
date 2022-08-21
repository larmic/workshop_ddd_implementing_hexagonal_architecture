package de.larmic.ddd.infrastructure.rest

import de.larmic.ddd.application.RaumHinzufuegen
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class RoomRestController(private val raumRepository: RaumRepository, private val raumHinzufuegen: RaumHinzufuegen) {

    @PostMapping(value = ["/api/room"], consumes = ["application/json"], produces = ["application/json"])
    fun postRoom(@RequestBody dto: CreateRoomDto): ResponseEntity<Any> {
        return when (val result = raumHinzufuegen.fuegeRaumHinzu(dto.mapToDomain())) {
            is RaumHinzufuegen.Ok -> ResponseEntity.ok(result.raum.mapToDto())
            RaumHinzufuegen.RaumExistiertBereits -> ResponseEntity.badRequest().body("Room number ${dto.number} already exists")
        }
    }

    @GetMapping(value = ["/api/room/{id}"])
    fun getRoom(@PathVariable id: String): ResponseEntity<Any> {
        val raum = raumRepository.finde(Raum.Id(UUID.fromString(id))) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(raum.mapToDto())
    }
}

class CreateRoomDto(val number: String, val name: String)
class ReadRoomDto(val id: String, val number: String, val name: String)

private fun CreateRoomDto.mapToDomain() = Raum(
    nummer = Raum.Nummer(value = this.number),
    name = Raum.Name(value = this.name)
)

private fun Raum.mapToDto() =
    ReadRoomDto(id = this.id.value.toString(), number = this.nummer.value, name = this.name.value)

