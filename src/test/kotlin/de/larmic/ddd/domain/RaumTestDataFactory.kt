package de.larmic.ddd.domain

fun createRaumTestData(
    raumNummer: String = "0815",
    raumName: String = "Ein kleiner Raum",
    persons: MutableList<Person> = mutableListOf()
) = Raum(nummer = Raum.Nummer(value = raumNummer), name = Raum.Name(value = raumName), persons = persons)
