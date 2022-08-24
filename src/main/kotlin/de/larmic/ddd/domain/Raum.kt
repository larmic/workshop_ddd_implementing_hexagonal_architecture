package de.larmic.ddd.domain

import de.larmic.ddd.common.AggregateRoot
import de.larmic.ddd.common.ValueObject
import java.util.*

@AggregateRoot
class Raum(
    val id: Id = Id(),
    val nummer: Nummer,
    val name: Name,
    private val persons: MutableList<Person> = mutableListOf()
) {

    // Innere Liste 'persons' ist nach aussen nicht sichtbar.
    // Nach Anforderung genügt es, nur die Kurzschreibweisen sichtbar zu machen.
    val personen: List<String>
        get() = this.persons.map { it.kurzschreibweise }

    fun fuegeHinzu(person: Person) {
        if (persons beinhaltet person) {
            throw IllegalArgumentException("Person '${person.kurzschreibweise}' is already part of this room")
        }

        this.persons.add(person)
    }

    @ValueObject
    data class Id(val value: UUID = UUID.randomUUID())

    @ValueObject
    data class Nummer(val value: String) {
        init {
            require(value.validateRoomNumber()) { "Room number $value must have 4 arbitrary digits" }
        }
    }

    @ValueObject
    class Name(value: String) {
        val value = value.normalizeName()

        init {
            require(value.isNotBlank()) { "Room name should not be empty" }
        }
    }
}

private fun String.normalizeName() = trim { it <= ' ' }
private fun String.validateRoomNumber() = this.length == 4 && this.isNumeric()
private fun String.isNumeric() = this.all { it.isDigit() }
private infix fun List<Person>.beinhaltet(person: Person) =
    this.map { it.id }.contains(person.id) || this.map { it.ldap.value }.contains(person.ldap.value)