package de.neusta.larmic.ddd.domain

interface RaumRepository {

    fun speichere(raum: Raum)
    fun finde(nummer: Raum.Nummer): Raum?
    fun existiert(nummer: Raum.Nummer): Boolean

}