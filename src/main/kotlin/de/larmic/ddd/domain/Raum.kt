package de.larmic.ddd.domain

import de.larmic.ddd.common.AggregateRoot
import de.larmic.ddd.common.ValueObject
import java.util.*

@AggregateRoot
class Raum(val id: Id = Id(), val nummer: Nummer, val name: Name) {

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