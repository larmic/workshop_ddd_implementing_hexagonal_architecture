package de.larmic.ddd.infrastructure.common

import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.raum.Raum
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import java.util.*

fun MockMvc.getRoom(id: Raum.Id) = this.getRoom(id.value)
fun MockMvc.getRoom(id: String) = this.getRoom(UUID.fromString(id))
fun MockMvc.getRoom(id: UUID) = this.perform(get("/api/room/$id")).andDo(print())

fun MockMvc.postRoom(raum: Raum) =
    this.postRoom(json = """{"number": "${raum.nummer.value}", "name": "${raum.name.value}"}""")

fun MockMvc.postRoom(json: String) = this.perform(
    post("/api/room")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.trimIndent())
)
    .andDo(print())

fun MockMvc.getPerson(id: Person.Id) = this.getPerson(id.value)
fun MockMvc.getPerson(id: UUID) = this.perform(get("/api/person/$id")).andDo(print())

fun MockMvc.postPerson(person: Person) = this.postPerson(json = person.toJson())

fun MockMvc.postPerson(json: String) = this.perform(
    post("/api/person")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json.trimIndent())
)
    .andDo(print())

fun MockMvc.putPersonToRoom(raumId: Raum.Id, personId: Person.Id) =
    this.putPersonToRoom(roomId = raumId.value.toString(), personId = personId.value.toString())

fun MockMvc.putPersonToRoom(roomId: String, personId: String) =
    this.perform(put("/api/room/$roomId/person/$personId")).andDo(print())

private fun Person.toJson() = """
            {
                "firstName": "${this.vorname.value}",
                "lastName": "${this.nachname.value}",
                "ldap": "${this.ldap.value}",
                "addition" : "${this.namenszusatz?.value.orEmpty()}"
            }
        """.trimIndent()