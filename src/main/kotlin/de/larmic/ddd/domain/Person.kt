package de.larmic.ddd.domain

import de.larmic.ddd.common.Entity
import de.larmic.ddd.common.ValueObject
import java.util.*

@Entity(id = "id")
class Person(
    val id: Id = Id(),
    val vorname: Vorname,
    val nachname: Nachname,
    val ldap: Ldap,
    val titel: Titel? = null,
    val namenszusatz: Namenszusatz? = null
) {

    val fullName: String
        get() {
            return "${titel.asString()} ${vorname.value} ${namenszusatz.asString()} ${nachname.value} (${ldap.value})"
                .removeDuplicatedWhiteSpaces()
                .trim()
        }

    data class Id(val id: UUID = UUID.randomUUID())

    @ValueObject
    // TODO rename inner fields to value
    class Vorname(vorname: String) {
        val value: String = vorname.normalizeName()

        init {
            require(vorname.isNotBlank()) { "First name must not be empty" }
        }
    }

    @ValueObject
    class Nachname(nachname: String) {
        val value: String = nachname.normalizeName()

        init {
            require(nachname.isNotBlank()) { "Last name must not be empty" }
        }
    }

    @ValueObject
    class Ldap(ldap: String) {
        val value: String = ldap.normalizeName()

        init {
            require(ldap.isNotBlank()) { "LDAP user must not be empty" }
        }
    }

    @ValueObject
    enum class Namenszusatz(val label: String) {

        VON("von"),
        VAN("van"),
        DE("de");

        companion object Factory {
            fun create(label: String?) = if (label.isNullOrBlank()) {
                null
            } else {
                label.mapToAddition() ?: throw IllegalArgumentException("Person addition '$label' is not supported")
            }

            private fun String.mapToAddition() = Namenszusatz.values().firstOrNull { it.label == this.trimAndLowercase() }
            private fun String.trimAndLowercase() = this.trim { it <= ' ' }.lowercase()
        }
    }

    @ValueObject
    enum class Titel(val label: String) {

        DR("Dr.");

        companion object Factory {
            fun create(label: String?) = if (label.isNullOrBlank()) {
                null
            } else {
                label.mapToAddition() ?: throw IllegalArgumentException("Person title '$label' is not supported")
            }

            private fun String.mapToAddition() = Titel.values().firstOrNull { it.label.lowercase() == this.trimAndLowercase() }
            private fun String.trimAndLowercase() = this.trim { it <= ' ' }.lowercase()
        }
    }
}

private fun Person.Titel?.asString() = this?.label ?: ""
private fun Person.Namenszusatz?.asString() = this?.label ?: ""
private fun String.removeDuplicatedWhiteSpaces() = this.replace("\\s+".toRegex(), " ")
private fun String.normalizeName() = trim { it <= ' ' }