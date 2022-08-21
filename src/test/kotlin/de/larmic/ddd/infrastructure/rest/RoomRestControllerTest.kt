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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
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
        val raum = createRaumTestData(raumNummer = "0007", raumName = "James Room")

        every { raumHinzufuegenMock.fuegeRaumHinzu(any()) } returns Ok(raum = raum)

        this.mockMvc.postRoom(raum = raum)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(raum.id.value.toString()))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))

        verify {
            raumHinzufuegenMock.fuegeRaumHinzu(withArg {
                assertThat(it).isNotNull
                assertThat(it.nummer.value).isEqualTo(raum.nummer.value)
                assertThat(it.name.value).isEqualTo(raum.name.value)
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
        val raum = createRaumTestData()

        every { raumHinzufuegenMock.fuegeRaumHinzu(any()) } returns RaumExistiertBereits

        val response = this.mockMvc.postRoom(raum = raum)
            .andExpect(status().is4xxClientError)
            .andReturn().response.contentAsString

        assertThat(response).isEqualTo("Room number ${raum.nummer.value} already exists")
    }

    @Test
    internal fun `get an existing room`() {
        val raum = createRaumTestData()

        every { raumRepositoryMock.finde(id = raum.id) } returns raum

        this.mockMvc.getRoom(id = raum.id)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(raum.id.value.toString()))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
    }

    @Test
    internal fun `get a not existing room`() {
        val raumId = Raum.Id()
        every { raumRepositoryMock.finde(id = raumId) } returns null

        this.mockMvc.getRoom(raumId)
            .andExpect(status().isNotFound)
    }
}
