package de.larmic.ddd

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.larmic.ddd.infrastructure.common.getRoom
import de.larmic.ddd.infrastructure.common.postRoom
import de.larmic.ddd.infrastructure.common.putPersonToRoom
import de.larmic.ddd.infrastructure.rest.ReadRoomDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * User story acceptance tests.
 */
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StoryIT {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    internal fun `create room, add person and load room`() {
        val raum = createRaumTestData()
        val person = createPersonTestData(titel = Person.Titel.DR, namenszusatz = Person.Namenszusatz.VON)

        // post room
        val roomId = this.mockMvc.postRoom(raum = raum)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
            .andExpect(jsonPath("$.persons").isArray)
            .andExpect(jsonPath("$.persons").isEmpty)
            .andReturnReadRoomDto().id

        // add person to room
        this.mockMvc.putPersonToRoom(roomId = roomId, json = """
            {
                "firstName": "${person.vorname.value}",
                "lastName": "${person.nachname.value}",
                "ldap": "${person.ldap.value}",
                "title": "${person.titel?.value}",
                "addition" : "${person.namenszusatz?.value}"
            }
        """.trimIndent())
            .andExpect(status().isOk)

        // read room
        this.mockMvc.getRoom(id = roomId)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(roomId))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
            .andExpect(jsonPath("$.persons[0]").value(person.kurzschreibweise))
            .andExpect(jsonPath("$.persons.length()").value(1))
    }
}

private fun ResultActions.andReturnReadRoomDto() = this.andReturn().mapToReadRoomDto()
private fun MvcResult.mapToReadRoomDto() = this.response.contentAsString.readJsonValue()
private fun String.readJsonValue() = jacksonObjectMapper().readValue(this, ReadRoomDto::class.java)
