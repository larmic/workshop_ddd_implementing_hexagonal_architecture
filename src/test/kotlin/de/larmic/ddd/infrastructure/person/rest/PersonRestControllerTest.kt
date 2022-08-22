package de.larmic.ddd.infrastructure.person.rest

import com.ninjasquad.springmockk.MockkBean
import de.larmic.ddd.application.person.PersonHinzufuegen
import de.larmic.ddd.domain.person.Person
import de.larmic.ddd.domain.person.PersonRepository
import de.larmic.ddd.domain.person.createPersonTestData
import de.larmic.ddd.infrastructure.common.getPerson
import de.larmic.ddd.infrastructure.common.postPerson
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PersonRestController::class)
internal class PersonRestControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean(relaxed = true)
    private lateinit var personRepositoryMock: PersonRepository

    @MockkBean
    private lateinit var personHinzufuegenMock: PersonHinzufuegen

    @Test
    internal fun `post a new person with required fields`() {
        val person = createPersonTestData()

        every { personHinzufuegenMock.fuegePersonHinzu(any()) } returns PersonHinzufuegen.Ok(person = person)

        this.mockMvc.postPerson(person = person)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(person.id.value.toString()))
            .andExpect(jsonPath("$.firstName").value(person.vorname.value))
            .andExpect(jsonPath("$.lastName").value(person.nachname.value))
            .andExpect(jsonPath("$.ldap").value(person.ldap.value))
            .andExpect(jsonPath("$.title").doesNotExist())
            .andExpect(jsonPath("$.addition").doesNotExist())

        verify {
            personHinzufuegenMock.fuegePersonHinzu(withArg {
                assertThat(it).isNotNull
                assertThat(it.vorname.value).isEqualTo(person.vorname.value)
                assertThat(it.nachname.value).isEqualTo(person.nachname.value)
                assertThat(it.ldap.value).isEqualTo(person.ldap.value)
                assertThat(it.titel?.value).isEqualTo(person.titel?.value)
                assertThat(it.namenszusatz?.value).isEqualTo(person.namenszusatz?.value)
            })
        }
    }

    @Test
    internal fun `post a new person with optional fields`() {
        val person = createPersonTestData(titel = Person.Titel.DR, namenszusatz = Person.Namenszusatz.DE)

        every { personHinzufuegenMock.fuegePersonHinzu(any()) } returns PersonHinzufuegen.Ok(person = person)

        this.mockMvc.postPerson(person = person)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(person.id.value.toString()))
            .andExpect(jsonPath("$.firstName").value(person.vorname.value))
            .andExpect(jsonPath("$.lastName").value(person.nachname.value))
            .andExpect(jsonPath("$.ldap").value(person.ldap.value))
            .andExpect(jsonPath("$.title").value(person.titel!!.value))
            .andExpect(jsonPath("$.addition").value(person.namenszusatz!!.value))

        verify {
            personHinzufuegenMock.fuegePersonHinzu(withArg {
                assertThat(it).isNotNull
                assertThat(it.vorname.value).isEqualTo(person.vorname.value)
                assertThat(it.nachname.value).isEqualTo(person.nachname.value)
                assertThat(it.ldap.value).isEqualTo(person.ldap.value)
                assertThat(it.titel?.value).isEqualTo(person.titel!!.value)
                assertThat(it.namenszusatz?.value).isEqualTo(person.namenszusatz!!.value)
            })
        }
    }

    @Test
    internal fun `post a not valid room`() {
        this.mockMvc.postPerson(json = "{}")
            .andExpect(status().is4xxClientError)
    }

    @Test
    internal fun `get an existing person`() {
        val person = createPersonTestData()

        every { personRepositoryMock.finde(id = person.id) } returns person

        this.mockMvc.getPerson(id = person.id)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(person.id.value.toString()))
            .andExpect(jsonPath("$.firstName").value(person.vorname.value))
            .andExpect(jsonPath("$.lastName").value(person.nachname.value))
            .andExpect(jsonPath("$.ldap").value(person.ldap.value))
            .andExpect(jsonPath("$.title").isEmpty)
            .andExpect(jsonPath("$.addition").isEmpty)
    }

    @Test
    internal fun `get a not existing person`() {
        every { personRepositoryMock.finde(id = any()) } returns null

        this.mockMvc.getPerson(Person.Id())
            .andExpect(status().isNotFound)
    }
}