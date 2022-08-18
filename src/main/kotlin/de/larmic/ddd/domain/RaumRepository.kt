package de.larmic.ddd.domain

@Repository
interface RaumRepository {

    fun speichere(raum: Raum)
    fun finde(nummer: Raum.Nummer): Raum?
    fun existiert(nummer: Raum.Nummer): Boolean

}