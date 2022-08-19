package de.larmic.ddd

import de.larmic.ddd.infrastructure.common.getRoom
import de.larmic.ddd.infrastructure.common.postRoom
import de.larmic.ddd.infrastructure.common.putPersonToRoom
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
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
        val roomNumber = "0007"
        val roomName = "James Room"

        // create a new room
        this.mockMvc.postRoom(
            json = """{"number": "$roomNumber", "name": "$roomName"}"""
        ).andExpect(status().isOk)

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

        // load room
        val response = this.mockMvc.getRoom(roomNumber)
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        JSONAssert.assertEquals(
            """{"number":  "$roomNumber", "name":  "$roomName", "persons": ["Dr. Lars von Michaelis (lamichae)"] }""",
            response,
            JSONCompareMode.STRICT
        )
    }
}