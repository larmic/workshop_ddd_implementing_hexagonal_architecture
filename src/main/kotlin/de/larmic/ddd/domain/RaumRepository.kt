package de.larmic.ddd.domain

@Repository
interface RaumRepository {

    fun legeAn(raum: Raum)
    fun finde(nummer: Raum.Nummer): Raum?
    fun existiert(nummer: Raum.Nummer): Boolean

}