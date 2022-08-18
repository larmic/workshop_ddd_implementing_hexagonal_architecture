package de.larmic.ddd.domain

@AggregateRoot(id = "nummer") // is it a good idea to use a natural key as an id?
class Raum(val nummer: Nummer, val name: Name) {

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