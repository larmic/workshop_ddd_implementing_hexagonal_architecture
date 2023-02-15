package de.larmic.ddd.domain.raum

import de.larmic.ddd.common.AggregateRoot
import de.larmic.ddd.common.ValueObject
import java.util.*

@AggregateRoot
class Raum(
    val id: Id = Id(),
    val nummer: Nummer,
    val name: Name,
    private val personIds: MutableList<PersonRefId> = mutableListOf()
) {

    // Innere Liste 'persons' ist nach aussen nicht sichtbar.
    // Nach Anforderung genügt es, nur die Kurzschreibweisen sichtbar zu machen.
    val personenIds: List<PersonRefId>
        get() = this.personIds

    fun fuegeHinzu(personRefId: PersonRefId) {
        if (personIds beinhaltet personRefId) {
            throw IllegalArgumentException("Person '$personRefId' is already part of this room")
        }

        this.personIds.add(personRefId)
    }

    @ValueObject
    data class Id(val value: UUID = UUID.randomUUID())

    // Man könnte an dieser Stelle auch direkt Person.Id verwenden.
    // Eine Referenz ermöglicht hier die Entkopplung. Damit könnten beide Bereiche in eigene Domänen aufgeteilt werden.
    @ValueObject
    data class PersonRefId(val value: UUID)

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
private infix fun List<Raum.PersonRefId>.beinhaltet(personRefId: Raum.PersonRefId) = this.contains(personRefId)