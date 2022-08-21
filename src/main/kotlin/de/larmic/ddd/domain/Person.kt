package de.larmic.ddd.domain

import de.larmic.ddd.common.Entity
import de.larmic.ddd.common.ValueObject
import java.util.*

@Entity
class Person(
    val id: Id = Id(),
    val vorname: Vorname,
    val nachname: Nachname,
    val ldap: Ldap,
    val titel: Titel? = null,
    val namenszusatz: Namenszusatz? = null
) {

    @ValueObject
    val kurzschreibweise: String
        get() {
            return "${titel.asString()} ${vorname.value} ${namenszusatz.asString()} ${nachname.value} (${ldap.value})"
                .removeDuplicatedWhiteSpaces()
                .trim()
        }

    @ValueObject
    data class Id(val value: UUID = UUID.randomUUID())

    @ValueObject
    class Vorname(value: String) {
        val value: String = value.normalizeName()

        init {
            require(value.isNotBlank()) { "First name must not be empty" }
        }
    }

    @ValueObject
    class Nachname(value: String) {
        val value: String = value.normalizeName()

        init {
            require(value.isNotBlank()) { "Last name must not be empty" }
        }
    }

    @ValueObject
    class Ldap(value: String) {
        val value: String = value.normalizeName()

        init {
            require(value.isNotBlank()) { "LDAP user must not be empty" }
        }
    }

    @ValueObject
    enum class Namenszusatz(val value: String) {

        VON("von"),
        VAN("van"),
        DE("de");

        companion object Factory {
            fun create(label: String?) = if (label.isNullOrBlank()) {
                null
            } else {
                label.mapToAddition() ?: throw IllegalArgumentException("Person addition '$label' is not supported")
            }

            private fun String.mapToAddition() = Namenszusatz.values().firstOrNull { it.value == this.trimAndLowercase() }
            private fun String.trimAndLowercase() = this.trim { it <= ' ' }.lowercase()
        }
    }

    @ValueObject
    enum class Titel(val value: String) {

        DR("Dr.");

        companion object Factory {
            fun create(label: String?) = if (label.isNullOrBlank()) {
                null
            } else {
                label.mapToAddition() ?: throw IllegalArgumentException("Person title '$label' is not supported")
            }

            private fun String.mapToAddition() = Titel.values().firstOrNull { it.value.lowercase() == this.trimAndLowercase() }
            private fun String.trimAndLowercase() = this.trim { it <= ' ' }.lowercase()
        }
    }
}

private fun Person.Titel?.asString() = this?.value ?: ""
private fun Person.Namenszusatz?.asString() = this?.value ?: ""
private fun String.removeDuplicatedWhiteSpaces() = this.replace("\\s+".toRegex(), " ")
private fun String.normalizeName() = trim { it <= ' ' }