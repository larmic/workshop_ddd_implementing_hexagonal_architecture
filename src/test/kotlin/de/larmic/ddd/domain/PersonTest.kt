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
            val person = createPersonTestData(vorname = "Uwe", nachname = "Svensson", ldap = "usvens")
            assertThat(person.id).isNotNull
            assertThat(person.vorname.value).isEqualTo("Uwe")
            assertThat(person.nachname.value).isEqualTo("Svensson")
            assertThat(person.ldap.value).isEqualTo("usvens")
        }

        @Test
        fun `has minimal not trimmed attributes`() {
            val person = createPersonTestData(vorname = " Uwe ", nachname = " Svensson ", ldap = " usvens ")
            assertThat(person.id).isNotNull
            assertThat(person.vorname.value).isEqualTo("Uwe")
            assertThat(person.nachname.value).isEqualTo("Svensson")
            assertThat(person.ldap.value).isEqualTo("usvens")
            assertThat(person.namenszusatz).isNull()
            assertThat(person.titel).isNull()
        }

        @ParameterizedTest
        @EnumSource(Person.Namenszusatz::class)
        fun `has Anrede`(namenszusatz: Person.Namenszusatz) {
            val person = createPersonTestData(namenszusatz = namenszusatz)
            assertThat(person.namenszusatz).isEqualTo(namenszusatz)
        }

        @Test
        fun `has Titel`() {
            val person = createPersonTestData(titel = Person.Titel.DR)
            assertThat(person.titel).isEqualTo(Person.Titel.DR)
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
        fun `has empty ldap user name`(ldapUser: String) {
            assertThatThrownBy { createPersonTestData(ldap = ldapUser) }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("LDAP user must not be empty")
        }
    }

    @Nested
    @DisplayName("Test Kurzschreibweise with")
    inner class Kurzschreibweise {
        @Test
        internal fun `person has no title or addition`() {
            val person = createPersonTestData(vorname = "Susanne", nachname = "Moog", ldap = "smoog")
            assertThat(person.kurzschreibweise).isEqualTo("Susanne Moog (smoog)")
        }

        @Test
        internal fun `person has Title but no Anrede`() {
            val person = createPersonTestData(titel = Person.Titel.DR)
            assertThat(person.kurzschreibweise).isEqualTo("Dr. Uwe Svensson (usvens)")
        }

        @ParameterizedTest
        @EnumSource(Person.Namenszusatz::class)
        internal fun `person has Anrede but no Titel`(namenszusatz: Person.Namenszusatz) {
            val person = createPersonTestData(vorname = "Alexander", nachname = "Cole", ldap = "acole", namenszusatz = namenszusatz)
            assertThat(person.kurzschreibweise).isEqualTo("Alexander ${namenszusatz.value} Cole (acole)")
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

    @Nested
    @DisplayName("Test Titel with")
    inner class PersonTitle {

        @Test
        fun `get value by label`() {
            assertThat(Person.Titel.create("Dr.")).isEqualTo(Person.Titel.DR)
            assertThat(Person.Titel.create(" Dr. ")).isEqualTo(Person.Titel.DR)
        }

        @Test
        fun `get value by label when label is empty`() {
            assertThat(Person.Titel.create("")).isNull()
            assertThat(Person.Titel.create(" ")).isNull()
        }

        @Test
        fun `get value by label when label is null`() {
            assertThat(Person.Titel.create(null)).isNull()
        }

        @Test
        fun `get value by label when label is unknown`() {
            assertThatThrownBy { Person.Titel.create("not-known") }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("Person title 'not-known' is not supported")
        }
    }
}