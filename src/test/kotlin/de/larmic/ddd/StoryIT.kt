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

    private val objectMapper = jacksonObjectMapper()

    @Test
    internal fun `create room, add person and load room`() {
        val roomNumber = "0007"
        val roomName = "James Room"

        val roomId = this.mockMvc.postRoom(json = """{"number": "$roomNumber", "name": "$roomName"}""")
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.number").value(roomNumber))
            .andExpect(jsonPath("$.name").value(roomName))
            .andReturnReadRoomDto().id

        // add person to room
        this.mockMvc.putPersonToRoom(roomNumber = roomNumber, json = """
            {
                "firstName": "Lars",
                "lastName": "Michaelis",
                "ldap": "lamichae",
                "title": "Dr.",
                "addition" : "von"
            }
        """.trimIndent())

        this.mockMvc.getRoom(roomId)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(roomId))
            .andExpect(jsonPath("$.number").value(roomNumber))
            .andExpect(jsonPath("$.name").value(roomName))
        // TODO verify persons
    }

    private fun ResultActions.andReturnReadRoomDto() = this.andReturn().mapToReadRoomDto()
    private fun MvcResult.mapToReadRoomDto() = this.response.contentAsString.readJsonValue()
    private fun String.readJsonValue() = objectMapper.readValue(this, ReadRoomDto::class.java)
}

