package de.larmic.ddd.domain

import java.util.*

fun createPersonTestData(
    id: UUID = UUID.randomUUID(),
    vorname: String = "Lars",
    nachname: String = "Michaelis",
    benutzername: String = "larmic",
    namenszusatz: Person.Namenszusatz? = null,
) = Person(
    id = Person.Id(id),
    vorname = Person.Vorname(vorname),
    nachname = Person.Nachname(nachname),
    benutzername = Person.Benutzername(benutzername),
    namenszusatz = namenszusatz
)