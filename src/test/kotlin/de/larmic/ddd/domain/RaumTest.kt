package de.larmic.ddd.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

internal class RaumTest {

    @Nested
    @DisplayName("Create Raum.Nummer with Nummer")
    inner class RaumNummerTests {

        @ParameterizedTest
        @ValueSource(strings = ["1234", "0023", "0000", "9999"])
        internal fun `has 4 digits`(number: String) {
            // verify no exception is thrown. Happy path!
            assertThat(Raum.Nummer(number).value).isEqualTo(number)
        }

        @ParameterizedTest
        @ValueSource(strings = ["123", "03", "1", "990"])
        internal fun `has less than 4 digits`(number: String) {
            assertThatThrownBy { Raum.Nummer(number) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Room number $number must have 4 arbitrary digits")
        }

        @ParameterizedTest
        @ValueSource(strings = ["A123", "BBBB", "879I"])
        internal fun `contains letters`(number: String) {
            assertThatThrownBy { Raum.Nummer(number) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Room number $number must have 4 arbitrary digits")
        }

        @ParameterizedTest
        @ValueSource(strings = ["    ", " 123", " 23 "])
        internal fun `contains empty characters`(number: String) {
            assertThatThrownBy { Raum.Nummer(number) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Room number $number must have 4 arbitrary digits")
        }
    }

    @Nested
    @DisplayName("Create Raum.Name with Name")
    inner class RaumNameTests {

        @ParameterizedTest
        @ValueSource(strings = ["R1", "Raum17", "Z"])
        internal fun `is not empty`(name: String) {
            // verify no exception is thrown. Happy path!
            assertThat(Raum.Name(name).value).isEqualTo(name)
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "      "])
        internal fun `is  empty`(name: String) {
            assertThatThrownBy { Raum.Name(name) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Room name should not be empty")
        }

        @ParameterizedTest
        @ValueSource(strings = [" room1", "room1 ", " room1 "])
        internal fun `value is not trimmed`(name: String) {
            assertThat(Raum.Name(name).value).isEqualTo("room1")
        }
    }

    @Nested
    @DisplayName("Create Raum Personen with")
    inner class RaumPersonsTests {

        @Test
        internal fun `persons are empty`() {
            val raum = createRaumTestData()

            assertThat(raum.personen).isEmpty()
        }

        @Test
        internal fun `persons are not empty`() {
            val raum = createRaumTestData()

            raum.fuegeHinzu(createPersonTestData(vorname = "Lars", nachname = "Michaelis", ldap = "lamichae"))
            raum.fuegeHinzu(createPersonTestData(vorname = "Lars", nachname = "Mühlmann", ldap = "lamueh", titel = Person.Titel.DR))

            assertThat(raum.personen)
                .containsExactlyInAnyOrder(
                    "Lars Michaelis (lamichae)",
                    "Dr. Lars Mühlmann (lamueh)"
                )
        }

        @Test
        internal fun `person already exists`() {
            val raum = createRaumTestData()
            val person = createPersonTestData(vorname = "Lars", nachname = "Michaelis", ldap = "lamichae")

            raum.fuegeHinzu(person)

            assertThatThrownBy { raum.fuegeHinzu(person) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Person '${person.fullName}' is already part of this room")
        }
    }

    @Test
    internal fun `create Raum with auto generated id`() {
        val number = "1234"
        val name = "Raum 1"

        val raum = createRaumTestData(raumNummer = number, raumName = name)

        assertThat(raum.id).isNotNull
        assertThat(raum.nummer.value).isEqualTo(number)
        assertThat(raum.name.value).isEqualTo(name)
    }

    @Test
    internal fun `create Raum with given id`() {
        val id = UUID.randomUUID()
        val number = "1234"
        val name = "Raum 1"

        val raum = createRaumTestData(id = id, raumNummer = number, raumName = name)

        assertThat(raum.id.value).isEqualTo(id)
        assertThat(raum.nummer.value).isEqualTo(number)
        assertThat(raum.name.value).isEqualTo(name)
    }
}