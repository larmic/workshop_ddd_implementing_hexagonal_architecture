package de.larmic.ddd.domain

import de.larmic.ddd.common.Entity
import de.larmic.ddd.common.ValueObject
import java.util.*

@Entity
class Person(
    val id: Id = Id(),
    val vorname: Vorname,
    val nachname: Nachname,
    val benutzername: Benutzername,
    val namenszusatz: Namenszusatz? = null
) {

    @ValueObject
    val kurzschreibweise: String
        get() {
            return "${vorname.value} ${namenszusatz.asString()} ${nachname.value} (${benutzername.value})"
                .entferneDoppelteLeerzeichen()
                .trim()
        }

    @ValueObject
    data class Id(val value: UUID = UUID.randomUUID())

    @ValueObject
    class Vorname(value: String) {
        val value: String = value.entferneLeerzeichen()

        init {
            require(value.isNotBlank()) { "First name must not be empty" }
        }
    }

    @ValueObject
    class Nachname(value: String) {
        val value: String = value.entferneLeerzeichen()

        init {
            require(value.isNotBlank()) { "Last name must not be empty" }
        }
    }

    @ValueObject
    class Benutzername(value: String) {
        val value: String = value.entferneLeerzeichen()

        init {
            require(value.isNotBlank()) { "Benutzername user must not be empty" }
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
}

private fun Person.Namenszusatz?.asString() = this?.value ?: ""
private fun String.entferneDoppelteLeerzeichen() = this.replace("\\s+".toRegex(), " ")
private fun String.entferneLeerzeichen() = trim { it <= ' ' }