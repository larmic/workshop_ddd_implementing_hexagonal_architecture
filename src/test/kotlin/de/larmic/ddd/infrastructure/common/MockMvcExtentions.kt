package de.larmic.ddd.infrastructure.common

import de.larmic.ddd.domain.Person
import de.larmic.ddd.domain.Raum
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import java.util.*

fun MockMvc.getRoom(id: Raum.Id) = this.getRoom(id.value)
fun MockMvc.getRoom(id: String) = this.getRoom(UUID.fromString(id))
fun MockMvc.getRoom(id: UUID) = this.perform(
    get("/api/room/$id")
)
    .andDo(print())

fun MockMvc.postRoom(raum: Raum) =
    this.postRoom(json = """{"number": "${raum.nummer.value}", "name": "${raum.name.value}"}""")

fun MockMvc.postRoom(json: String) = this.perform(
    post("/api/room")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.trimIndent())
)
    .andDo(print())

// TODO use damain building blocks
fun MockMvc.putPersonToRoom(raumId: Raum.Id, person: Person) = this.putPersonToRoom(roomId = raumId.value.toString(), json = person.toJson())
fun MockMvc.putPersonToRoom(roomId: String, json: String) = this.perform(
    put("/api/room/$roomId/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.trimIndent())
)
    .andDo(print())

private fun Person.toJson() = """
            {
                "firstName": "${this.vorname.value}",
                "lastName": "${this.nachname.value}",
                "ldap": "${this.ldap.value}",
                "addition" : "${this.namenszusatz?.value.orEmpty()}"
            }
        """.trimIndent()