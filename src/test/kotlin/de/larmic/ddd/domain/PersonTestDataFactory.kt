package de.larmic.ddd.domain

import de.neusta.larmic.ddd.domain.Namenszusatz
import de.neusta.larmic.ddd.domain.Titel
import java.util.*

fun createPersonTestData(
    id: UUID = UUID.randomUUID(),
    vorname: String = "Uwe",
    nachname: String = "Svensson",
    ldap: String = "usvens",
    titel: Titel? = null,
    namenszusatz: Namenszusatz? = null,
) = Person(
    id = Person.Id(id),
    vorname = Person.Vorname(vorname),
    nachname = Person.Nachname(nachname),
    ldap = Person.Ldap(ldap),
    titel = titel,
    namenszusatz = namenszusatz
)