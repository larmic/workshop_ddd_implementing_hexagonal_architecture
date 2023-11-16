package de.larmic.ddd.domain

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

internal class PersonTest {

    @Nested
    @DisplayName("Create person with person")
    inner class CreatePerson {
        @Test
        fun `has minimal attributes`() {
            val person = createPersonTestData(vorname = "Lars", nachname = "Michaelis", benutzername = "larmic")
            assertThat(person.id).isNotNull
            assertThat(person.vorname.value).isEqualTo("Lars")
            assertThat(person.nachname.value).isEqualTo("Michaelis")
            assertThat(person.benutzername.value).isEqualTo("larmic")
            assertThat(person.namenszusatz).isNull()
        }

        @Test
        fun `has minimal not trimmed attributes`() {
            val person = createPersonTestData(vorname = " Lars ", nachname = " Michaelis ", benutzername = " larmic ", namenszusatz = Person.Namenszusatz.VON)
            assertThat(person.id).isNotNull
            assertThat(person.vorname.value).isEqualTo("Lars")
            assertThat(person.nachname.value).isEqualTo("Michaelis")
            assertThat(person.benutzername.value).isEqualTo("larmic")
            assertThat(person.namenszusatz).isEqualTo(Person.Namenszusatz.VON)
        }

        @ParameterizedTest
        @EnumSource(Person.Namenszusatz::class)
        fun `has Anrede`(namenszusatz: Person.Namenszusatz) {
            val person = createPersonTestData(namenszusatz = namenszusatz)
            assertThat(person.namenszusatz).isEqualTo(namenszusatz)
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "   "])
        fun `has empty first name`(firstName: String) {
            assertThatThrownBy { createPersonTestData(vorname = firstName) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("First name must not be empty")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "   "])
        fun `has empty last name`(lastName: String) {
            assertThatThrownBy { createPersonTestData(nachname = lastName) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Last name must not be empty")
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "   "])
        fun `has empty Benutzername`(benutzername: String) {
            assertThatThrownBy { createPersonTestData(benutzername = benutzername) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Benutzername user must not be empty")
        }
    }

    @Nested
    @DisplayName("Test Kurzschreibweise with")
    inner class Kurzschreibweise {
        @Test
        internal fun `person has no addition`() {
            val person = createPersonTestData(vorname = "Maxina", nachname = "Musterfrau", benutzername = "muster")
            assertThat(person.kurzschreibweise).isEqualTo("Maxina Musterfrau (muster)")
        }

        @ParameterizedTest
        @EnumSource(Person.Namenszusatz::class)
        internal fun `person has Anrede but no Titel`(namenszusatz: Person.Namenszusatz) {
            val person = createPersonTestData(vorname = "Max", nachname = "Mustermann", benutzername = "maxmu", namenszusatz = namenszusatz)
            assertThat(person.kurzschreibweise).isEqualTo("Max ${namenszusatz.value} Mustermann (maxmu)")
        }
    }

    @Nested
    @DisplayName("Test Namenszusatz with")
    inner class Namenszusatz {

        @Test
        fun `get value by label`() {
            assertThat(Person.Namenszusatz.create("von")).isEqualTo(Person.Namenszusatz.VON)
            assertThat(Person.Namenszusatz.create(" von ")).isEqualTo(Person.Namenszusatz.VON)
            assertThat(Person.Namenszusatz.create("van")).isEqualTo(Person.Namenszusatz.VAN)
            assertThat(Person.Namenszusatz.create(" van ")).isEqualTo(Person.Namenszusatz.VAN)
            assertThat(Person.Namenszusatz.create("de")).isEqualTo(Person.Namenszusatz.DE)
            assertThat(Person.Namenszusatz.create(" de ")).isEqualTo(Person.Namenszusatz.DE)
        }

        @ParameterizedTest
        @ValueSource(strings = ["", " ", "   "])
        fun `get value by label when label is empty`(label: String) {
            assertThat(Person.Namenszusatz.create(label)).isNull()
        }

        @Test
        fun `get value by label when label is unknown`() {
            assertThatThrownBy { Person.Namenszusatz.create("not-known") }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Person addition 'not-known' is not supported")
        }
    }
}