package de.larmic.ddd.domain.person

import de.larmic.ddd.domain.person.Person
import java.util.*

fun createPersonTestData(
    id: UUID = UUID.randomUUID(),
    vorname: String = "Lars",
    nachname: String = "Michaelis",
    ldap: String = "larmic",
    titel: Person.Titel? = null,
    namenszusatz: Person.Namenszusatz? = null,
) = Person(
    id = Person.Id(id),
    vorname = Person.Vorname(vorname),
    nachname = Person.Nachname(nachname),
    ldap = Person.Ldap(ldap),
    titel = titel,
    namenszusatz = namenszusatz
)