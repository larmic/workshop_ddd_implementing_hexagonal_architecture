package de.larmic.ddd.infrastructure.rest

import com.ninjasquad.springmockk.MockkBean
import de.larmic.ddd.application.Ok
import de.larmic.ddd.application.PersonHinzufuegen
import de.larmic.ddd.application.RaumExistiertBereits
import de.larmic.ddd.application.RaumHinzufuegen
import de.larmic.ddd.domain.*
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
import java.util.*

@WebMvcTest(RoomRestController::class)
internal class RoomRestControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean(relaxed = true)
    private lateinit var raumRepositoryMock: RaumRepository

    @MockkBean
    private lateinit var raumHinzufuegenMock: RaumHinzufuegen

    @MockkBean
    private lateinit var personHinzufuegenMock: PersonHinzufuegen

    @Test
    internal fun `post a new valid room`() {
        val raum = createRaumTestData(raumNummer = "0007", raumName = "James Room")

        every { raumHinzufuegenMock.fuegeRaumHinzu(any()) } returns Ok(raum = raum)

        this.mockMvc.postRoom(
            json = """
            {
                "number": "${raum.nummer.value}",
                "name": "${raum.name.value}"
            }"""
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(raum.id.value.toString()))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
            .andExpect(jsonPath("$.persons").isArray)
            .andExpect(jsonPath("$.persons").isEmpty)
            .andReturn().response.contentAsString

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

        every { raumRepositoryMock.finde(raum.id) } returns raum

        this.mockMvc.getRoom(raum.id.value)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(raum.id.value.toString()))
            .andExpect(jsonPath("$.number").value(raum.nummer.value))
            .andExpect(jsonPath("$.name").value(raum.name.value))
        // TODO verify "persons": []
    }

    @Test
    internal fun `get a not existing room`() {
        val roomId = UUID.randomUUID()
        every { raumRepositoryMock.finde(Raum.Id(roomId)) } returns null

        this.mockMvc.getRoom(roomId)
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
            .andExpect(jsonPath("$.persons[0]").value(person.fullName))
            .andExpect(jsonPath("$.persons.length()").value(1))
    }

    @Test
    internal fun `put a person to an existing room`() {
        val roomId = Raum.Id()
        every { personHinzufuegenMock.fuegePersonZuRaumHinzu(any(), any()) } returns PersonHinzufuegen.Ok

        this.mockMvc.putPersonToRoom(roomId.value.toString(), """
            {
                "firstName": "Lars",
                "lastName": "Michaelis",
                "ldap": "lamichae",
                "title": "Dr.",
                "addition" : "von"
            }
        """.trimIndent())
            .andExpect(status().is2xxSuccessful)

        verify {
            personHinzufuegenMock.fuegePersonZuRaumHinzu(roomId, withArg {
                assertThat(it.vorname.value).isEqualTo("Lars")
                assertThat(it.nachname.value).isEqualTo("Michaelis")
                assertThat(it.ldap.value).isEqualTo("lamichae")
                assertThat(it.titel).isEqualTo(Person.Titel.DR)
                assertThat(it.namenszusatz).isEqualTo(Person.Namenszusatz.VON)
            })
        }
    }

    @Test
    internal fun `put a person to an not existing room`() {
        val roomNumber = "0815"
        every { personHinzufuegenMock.fuegePersonZuRaumHinzu(any(), any()) } returns PersonHinzufuegen.RaumNichtGefunden

        this.mockMvc.putPersonToRoom(roomNumber, """
            {
                "firstName": "Lars",
                "lastName": "Michaelis",
                "ldap": "lamichae",
                "title": "Dr.",
                "addition" : "von"
            }
        """.trimIndent())
            .andExpect(status().is4xxClientError)
    }
}
