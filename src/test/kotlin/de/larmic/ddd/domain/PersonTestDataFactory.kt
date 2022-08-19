package de.larmic.ddd.domain

import java.util.*

fun createPersonTestData(
    id: UUID = UUID.randomUUID(),
    vorname: String = "Uwe",
    nachname: String = "Svensson",
    ldap: String = "usvens",
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