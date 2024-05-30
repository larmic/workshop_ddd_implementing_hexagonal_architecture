package de.larmic.ddd.domain

import de.larmic.ddd.common.AggregateRoot
import de.larmic.ddd.common.ValueObject
import java.util.*

@AggregateRoot
class Raum(
    val id: Id = Id(),
    val nummer: Nummer,
    val name: Name,
    private val personen: MutableList<Person> = mutableListOf(),
) {

    // Innere Liste 'personen' ist nach aussen nicht sichtbar.
    // Nach Anforderung genügt es, nur die Kurzschreibweisen sichtbar zu machen.
    val personenkurzschreibweisen: List<String>
        get() = this.personen.map { it.kurzschreibweise }

    fun fuegeHinzu(person: Person) {
        if (personen beinhaltet person) {
            throw IllegalArgumentException("Person '${person.kurzschreibweise}' is already part of this room")
        }

        this.personen.add(person)
    }

    @ValueObject
    data class Id(val value: UUID = UUID.randomUUID())

    @ValueObject
    data class Nummer(val value: String) {
        init {
            require(value.validiereRaumnummer()) { "Room number $value must have 4 arbitrary digits" }
        }
    }

    @ValueObject
    class Name(value: String) {
        val value = value.entferneLeerzeichen()

        init {
            require(value.isNotBlank()) { "Room name should not be empty" }
        }
    }

}

private fun String.entferneLeerzeichen() = trim { it <= ' ' }
private fun String.validiereRaumnummer() = this.length == 4 && this.istEineZahl()
private fun String.istEineZahl() = this.all { it.isDigit() }
private infix fun List<Person>.beinhaltet(person: Person) =
    this.map { it.id }.contains(person.id) || this.map { it.benutzername.value }.contains(person.benutzername.value)