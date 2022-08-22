package de.larmic.ddd

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.createPersonTestData
import de.larmic.ddd.domain.raum.Raum
import de.larmic.ddd.domain.raum.createRaumTestData
import de.larmic.ddd.infrastructure.common.getRoom
import de.larmic.ddd.infrastructure.common.postPerson
import de.larmic.ddd.infrastructure.common.postRoom
import de.larmic.ddd.infrastructure.common.putPersonToRoom
import de.larmic.ddd.infrastructure.person.rest.ReadPersonDto
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
import java.util.*

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

        // post person
        val personId = this.mockMvc.postPerson(person = person)
            .andExpect(status().isOk)
            // TODO verify person fields
            .andReturnReadPersonDto()
            .mapToPersonId()

        // post room
        val raumId = this.mockMvc.postRoom(raum = raum)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
            .andExpect(jsonPath("$.persons").isArray)
            .andExpect(jsonPath("$.persons").isEmpty)
            .andReturnReadRoomDto()
            .mapToRaumId()

        // add person to room
        this.mockMvc.putPersonToRoom(raumId = raumId, personId = personId)
            .andExpect(status().isOk)

        // read room
        this.mockMvc.getRoom(id = raumId)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(raumId.value.toString()))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
            .andExpect(jsonPath("$.persons[0]").value(person.kurzschreibweise))
            .andExpect(jsonPath("$.persons.length()").value(1))
    }
}

private fun ResultActions.andReturnReadRoomDto() = this.andReturn().mapToReadRoomDto()
private fun ResultActions.andReturnReadPersonDto() = this.andReturn().mapToReadPersonDto()
private fun MvcResult.mapToReadRoomDto() = this.response.contentAsString.readRoomJsonValue()
private fun MvcResult.mapToReadPersonDto() = this.response.contentAsString.readPersonJsonValue()
private fun String.readRoomJsonValue() = jacksonObjectMapper().readValue(this, ReadRoomDto::class.java)
private fun String.readPersonJsonValue() = jacksonObjectMapper().readValue(this, ReadPersonDto::class.java)

private fun ReadRoomDto.mapToRaumId() = Raum.Id(UUID.fromString(this.id))
private fun ReadPersonDto.mapToPersonId() = Person.Id(UUID.fromString(this.id))
