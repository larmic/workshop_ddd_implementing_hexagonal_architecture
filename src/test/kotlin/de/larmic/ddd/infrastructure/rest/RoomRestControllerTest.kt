package de.larmic.ddd.infrastructure.rest

import com.ninjasquad.springmockk.MockkBean
import de.larmic.ddd.application.PersonZuRaumHinzufuegen
import de.larmic.ddd.application.RaumHinzufuegen
import de.larmic.ddd.domain.Raum
import de.larmic.ddd.domain.RaumRepository
import de.larmic.ddd.domain.createPersonTestData
import de.larmic.ddd.domain.createRaumTestData
import de.larmic.ddd.infrastructure.common.getRoom
import de.larmic.ddd.infrastructure.common.postRoom
import de.larmic.ddd.infrastructure.common.putPersonToRoom
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

    @MockkBean
    private lateinit var personZuRaumHinzufuegenMock: PersonZuRaumHinzufuegen

    @Test
    internal fun `post a new valid room`() {
        val raum = createRaumTestData(raumNummer = "0007", raumName = "James Room")

        every { raumHinzufuegenMock(any()) } returns RaumHinzufuegen.Ok(raum = raum)

        this.mockMvc.postRoom(raum = raum)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(raum.id.value.toString()))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
            .andExpect(jsonPath("$.persons").isArray)
            .andExpect(jsonPath("$.persons").isEmpty)
            .andReturn().response.contentAsString

        verify {
            raumHinzufuegenMock(withArg {
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

        every { raumHinzufuegenMock(any()) } returns RaumHinzufuegen.RaumExistiertBereits

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
            .andExpect(jsonPath("$.persons").isArray)
            .andExpect(jsonPath("$.persons").isEmpty)
    }

    @Test
    internal fun `get a not existing room`() {
        val raumId = Raum.Id()
        every { raumRepositoryMock.finde(id = raumId) } returns null

        this.mockMvc.getRoom(raumId)
            .andExpect(status().isNotFound)
    }

    @Test
    internal fun `get an existing room with existing persons`() {
        val person = createPersonTestData()
        val raum = createRaumTestData(persons = mutableListOf(person))

        every { raumRepositoryMock.finde(raum.id) } returns raum

        this.mockMvc.getRoom(raum.id.value)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(raum.id.value.toString()))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
            .andExpect(jsonPath("$.persons[0]").value(person.kurzschreibweise))
            .andExpect(jsonPath("$.persons.length()").value(1))
    }

    @Test
    internal fun `put a person to an existing room`() {
        val raumId = Raum.Id()
        val person = createPersonTestData()
        every { personZuRaumHinzufuegenMock(any(), any()) } returns PersonZuRaumHinzufuegen.Ok

        this.mockMvc.putPersonToRoom(raumId = raumId, person = person)
            .andExpect(status().is2xxSuccessful)

        verify {
            personZuRaumHinzufuegenMock(raumId, withArg {
                assertThat(it.vorname.value).isEqualTo(person.vorname.value)
                assertThat(it.nachname.value).isEqualTo(person.nachname.value)
                assertThat(it.benutzername.value).isEqualTo(person.benutzername.value)
                assertThat(it.namenszusatz).isNull()
            })
        }
    }

    @Test
    internal fun `put a person to an not existing room`() {
        val raumId = Raum.Id()
        every { personZuRaumHinzufuegenMock(any(), any()) } returns PersonZuRaumHinzufuegen.RaumNichtGefunden

        this.mockMvc.putPersonToRoom(raumId = raumId, person = createPersonTestData())
            .andExpect(status().is4xxClientError)
    }
}
