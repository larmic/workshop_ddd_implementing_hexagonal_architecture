package de.larmic.ddd.domain

import de.neusta.larmic.ddd.domain.Namenszusatz
import de.neusta.larmic.ddd.domain.Titel
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
        @EnumSource(Namenszusatz::class)
        fun `has Anrede`(namenszusatz: Namenszusatz) {
            val person = createPersonTestData(namenszusatz = namenszusatz)
            assertThat(person.namenszusatz).isEqualTo(namenszusatz)
        }

        @Test
        fun `has Titel`() {
            val person = createPersonTestData(titel = Titel.DR)
            assertThat(person.titel).isEqualTo(Titel.DR)
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
    @DisplayName("Test fullName with")
    inner class ToString {
        @Test
        internal fun `person has no title or addition`() {
            val person = createPersonTestData(vorname = "Susanne", nachname = "Moog", ldap = "smoog")
            assertThat(person.fullName).isEqualTo("Susanne Moog (smoog)")
        }

        @Test
        internal fun `person has Title but no Anrede`() {
            val person = createPersonTestData(titel = Titel.DR)
            assertThat(person.fullName).isEqualTo("Dr. Uwe Svensson (usvens)")
        }

        @ParameterizedTest
        @EnumSource(Namenszusatz::class)
        internal fun `person has Anrede but no Titel`(namenszusatz: Namenszusatz) {
            val person = createPersonTestData(vorname = "Alexander", nachname = "Cole", ldap = "acole", namenszusatz = namenszusatz)
            assertThat(person.fullName).isEqualTo("Alexander ${namenszusatz.label} Cole (acole)")
        }
    }
}