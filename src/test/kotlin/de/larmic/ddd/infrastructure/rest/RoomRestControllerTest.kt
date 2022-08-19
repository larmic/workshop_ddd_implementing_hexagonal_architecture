package de.larmic.ddd.infrastructure.rest

import com.ninjasquad.springmockk.MockkBean
import de.larmic.ddd.application.Ok
import de.larmic.ddd.application.RaumExistiertBereits
import de.larmic.ddd.application.RaumHinzufuegen
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository
import de.larmic.ddd.domain.createRaumTestData
import de.larmic.ddd.infrastructure.common.getRoom
import de.larmic.ddd.infrastructure.common.postRoom
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(RoomRestController::class)
internal class RoomRestControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean(relaxed = true)
    private lateinit var raumRepositoryMock: RaumRepository

    @MockkBean
    private lateinit var raumHinzufuegenMock: RaumHinzufuegen

    @Test
    internal fun `post a new valid room`() {
        val roomNumber = "0007"
        val roomName = "James Room"

        every { raumHinzufuegenMock.fuegeRaumHinzu(any()) } returns Ok

        this.mockMvc.postRoom(
            json = """
            {
                "number": "$roomNumber",
                "name": "$roomName"
            }"""
        ).andExpect(status().isOk)

        verify {
            raumHinzufuegenMock.fuegeRaumHinzu(withArg {
                assertThat(it).isNotNull
                assertThat(it.nummer.value).isEqualTo(roomNumber)
                assertThat(it.name.value).isEqualTo(roomName)
            })
        }
    }

    @Test
    internal fun `post a new room with number is not valid`() {
        val roomNumber = "not-valid"

        val response = this.mockMvc.postRoom(
            json = """
            {
                "number": "$roomNumber",
                "name": "James Room"
            }"""
        )
            .andExpect(status().is4xxClientError)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo("Room number $roomNumber must have 4 arbitrary digits")
    }

    @Test
    internal fun `post an existing room`() {
        val roomNumber = "0007"

        every { raumHinzufuegenMock.fuegeRaumHinzu(any()) } returns RaumExistiertBereits

        val response = this.mockMvc.postRoom(
            json = """
            {
                "number": "$roomNumber",
                "name": "Bond Room"
            }"""
        )
            .andExpect(status().is4xxClientError)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo("Room number $roomNumber already exists")
    }

    @Test
    internal fun `get an existing room`() {
        val raum = createRaumTestData()

        every { raumRepositoryMock.finde(raum.nummer) } returns raum

        val response = this.mockMvc.getRoom(raum.nummer.value)
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        JSONAssert.assertEquals(
            """{"number":  "${raum.nummer.value}", "name":  "${raum.name.value}"}""",
            response,
            JSONCompareMode.STRICT
        )
    }

    @Test
    internal fun `get a not existing room`() {
        val roomNumber = "0815"
        every { raumRepositoryMock.finde(Raum.Nummer(roomNumber)) } returns null

        this.mockMvc.getRoom(roomNumber)
            .andExpect(status().isNotFound)
    }
}
