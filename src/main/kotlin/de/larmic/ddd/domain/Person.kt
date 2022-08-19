package de.larmic.ddd.domain

import de.neusta.larmic.ddd.domain.Namenszusatz
import de.neusta.larmic.ddd.domain.Titel
import java.util.*

// entity
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

    class Vorname(vorname: String) {
        val value: String = vorname.normalizeName()

        init {
            require(vorname.isNotBlank()) { "First name must not be empty" }
        }
    }

    class Nachname(nachname: String) {
        val value: String = nachname.normalizeName()

        init {
            require(nachname.isNotBlank()) { "Last name must not be empty" }
        }
    }

    class Ldap(ldap: String) {
        val value: String = ldap.normalizeName()

        init {
            require(ldap.isNotBlank()) { "LDAP user must not be empty" }
        }
    }
}

private fun Titel?.asString() = this?.label ?: ""
private fun Namenszusatz?.asString() = this?.label ?: ""
private fun String.removeDuplicatedWhiteSpaces() = this.replace("\\s+".toRegex(), " ")
private fun String.normalizeName() = trim { it <= ' ' }