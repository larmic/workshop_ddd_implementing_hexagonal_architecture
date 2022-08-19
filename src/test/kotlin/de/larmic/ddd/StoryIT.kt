package de.larmic.ddd

import de.larmic.ddd.infrastructure.common.getRoom
import de.larmic.ddd.infrastructure.common.postRoom
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
    internal fun `create and load room`() {
        val roomNumber = "0007"
        val roomName = "James Room"

        this.mockMvc.postRoom(
            json = """{"number": "$roomNumber", "name": "$roomName"}"""
        ).andExpect(status().isOk)

        val response = this.mockMvc.getRoom(roomNumber)
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        JSONAssert.assertEquals(
            """{"number":  "$roomNumber", "name":  "$roomName"}""",
            response,
            JSONCompareMode.STRICT
        )
    }
}