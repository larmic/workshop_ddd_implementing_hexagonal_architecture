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

            assertThat(raum.personenkurzschreibweisen).isEmpty()
        }

        @Test
        internal fun `persons are not empty`() {
            val raum = createRaumTestData()

            raum.fuegeHinzu(createPersonTestData(vorname = "Lars", nachname = "Michaelis", ldap = "lamichae"))
            raum.fuegeHinzu(createPersonTestData(vorname = "Lars", nachname = "Mühlmann", ldap = "lamueh", namenszusatz = Person.Namenszusatz.VON))

            assertThat(raum.personenkurzschreibweisen)
                .containsExactlyInAnyOrder(
                    "Lars Michaelis (lamichae)",
                    "Lars von Mühlmann (lamueh)"
                )
        }

        @Test
        internal fun `person already exists by id`() {
            val raum = createRaumTestData()
            val personId = UUID.randomUUID()
            val person1 = createPersonTestData(id = personId, ldap = "ldap1")
            val person2 = createPersonTestData(id = personId, ldap = "ldap2")

            raum.fuegeHinzu(person1)

            assertThatThrownBy { raum.fuegeHinzu(person2) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Person '${person2.kurzschreibweise}' is already part of this room")
        }

        @Test
        internal fun `person already exists by ldap`() {
            val raum = createRaumTestData()
            val person1 = createPersonTestData(id = UUID.randomUUID(), ldap = "larmic")
            val person2 = createPersonTestData(id = UUID.randomUUID(), ldap = "larmic")

            raum.fuegeHinzu(person1)

            assertThatThrownBy { raum.fuegeHinzu(person2) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Person '${person2.kurzschreibweise}' is already part of this room")
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