package de.larmic.ddd.infrastructure.raum.rest

import com.ninjasquad.springmockk.MockkBean
import de.larmic.ddd.application.common.PersonZuRaumHinzufuegen
import de.larmic.ddd.application.common.RaumLaden
import de.larmic.ddd.application.raum.RaumHinzufuegen
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.createPersonTestData
import de.larmic.ddd.domain.raum.Raum
import de.larmic.ddd.domain.raum.createRaumTestData
import de.larmic.ddd.infrastructure.common.getRoom
import de.larmic.ddd.infrastructure.common.postRoom
import de.larmic.ddd.infrastructure.common.putPersonToRoom
import de.larmic.ddd.infrastructure.rest.RoomRestController
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

    @MockkBean
    private lateinit var raumLadenMock: RaumLaden

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
    internal fun `get an existing room without persons in it`() {
        val raum = createRaumTestData()
        val raumMitPersonen = RaumLaden.RaumMitPersonen(raum = raum, persons = emptyList())

        every { raumLadenMock.lade(id = raum.id) } returns RaumLaden.Ok(raumMitPersonen = raumMitPersonen)

        this.mockMvc.getRoom(id = raum.id)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(raum.id.value.toString()))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
            .andExpect(jsonPath("$.persons").isArray)
            .andExpect(jsonPath("$.persons").isEmpty)
    }

    @Test
    internal fun `get an existing room with persons in it`() {
        val raum = createRaumTestData()
        val person = createPersonTestData()
        val raumMitPersonen = RaumLaden.RaumMitPersonen(raum = raum, persons = listOf(person))

        every { raumLadenMock.lade(id = raum.id) } returns RaumLaden.Ok(raumMitPersonen = raumMitPersonen)

        this.mockMvc.getRoom(id = raum.id)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(raum.id.value.toString()))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
            .andExpect(jsonPath("$.persons").isArray)
            .andExpect(jsonPath("$.persons.length()").value(1))
            .andExpect(jsonPath("$.persons[0]").value(person.kurzschreibweise))
    }

    @Test
    internal fun `get a not existing room`() {
        val raumId = Raum.Id()
        every { raumLadenMock.lade(id = raumId) } returns RaumLaden.RaumNichtGefunden

        this.mockMvc.getRoom(raumId)
            .andExpect(status().isNotFound)
    }

    @Test
    internal fun `put a person to an existing room`() {
        val raumId = Raum.Id()
        val person = createPersonTestData()
        every { personZuRaumHinzufuegenMock(any(), any()) } returns PersonZuRaumHinzufuegen.Ok

        this.mockMvc.putPersonToRoom(raumId = raumId, personId = person.id)
            .andExpect(status().is2xxSuccessful)

        verify {
            personZuRaumHinzufuegenMock(raumId = raumId, personId = person.id)
        }
    }

    @Test
    internal fun `put a person to an not existing room`() {
        every {
            personZuRaumHinzufuegenMock(any(), any())
        } returns PersonZuRaumHinzufuegen.RaumNichtGefunden

        this.mockMvc.putPersonToRoom(raumId = Raum.Id(), personId = Person.Id())
            .andExpect(status().is4xxClientError)
    }
}
