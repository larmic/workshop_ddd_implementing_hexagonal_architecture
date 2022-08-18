package de.neusta.larmic.ddd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DDDApplication

fun main(args: Array<String>) {
    runApplication<DDDApplication>(*args)
}